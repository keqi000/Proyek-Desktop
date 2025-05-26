package game.util;

import java.io.*;
import java.util.Properties;

public class GameSettings {
    private static GameSettings instance;
    
    private int volume;
    private int brightness;
    private String difficulty;
    private String currentUser;
    
    private final String SETTINGS_FILE = "game_settings.properties";
    
    private GameSettings() {
        loadSettings();
    }
    
    public static synchronized GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    public void loadSettings() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(SETTINGS_FILE)) {
            props.load(in);
            
            volume = Integer.parseInt(props.getProperty("volume", "80"));
            brightness = Integer.parseInt(props.getProperty("brightness", "70"));
            difficulty = props.getProperty("difficulty", "Medium");
            currentUser = props.getProperty("currentUser", "Default");
            
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, use defaults
            volume = 80;
            brightness = 70;
            difficulty = "Medium";
            currentUser = "Default";
        } catch (IOException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void saveSettings() {
        Properties props = new Properties();
        props.setProperty("volume", String.valueOf(volume));
        props.setProperty("brightness", String.valueOf(brightness));
        props.setProperty("difficulty", difficulty);
        props.setProperty("currentUser", currentUser);
        
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Game Settings");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getters and setters
    public int getVolume() {
        return volume;
    }

    // Add a method to reload settings from file
    public void reloadSettings() {
        loadSettings();
    }

    
    public void setVolume(int volume) {
        this.volume = volume;
        saveSettings(); // Save immediately when changed
    }
    
    public int getBrightness() {
        return brightness;
    }
    
    public void setBrightness(int brightness) {
    this.brightness = brightness;
    saveSettings(); // Save immediately when changed
}
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
