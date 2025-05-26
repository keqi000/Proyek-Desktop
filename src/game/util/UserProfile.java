package game.util;

import java.io.*;
import java.util.Properties;

public class UserProfile {
    private String userName;
    private int highScore;
    private int level;
    private int totalPlayTime; // in minutes
    
    private static final String USER_DIR = "users";
    
    public UserProfile(String userName) {
        this.userName = userName;
        this.highScore = 0;
        this.level = 1;
        this.totalPlayTime = 0;
        
        // Create user directory if it doesn't exist
        File dir = new File(USER_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    public static UserProfile load(String userName) {
        UserProfile profile = new UserProfile(userName);
        
        File userFile = new File(USER_DIR + File.separator + userName + ".user");
        if (userFile.exists()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(userFile)) {
                props.load(in);
                
                profile.highScore = Integer.parseInt(props.getProperty("highScore", "0"));
                profile.level = Integer.parseInt(props.getProperty("level", "1"));
                profile.totalPlayTime = Integer.parseInt(props.getProperty("totalPlayTime", "0"));
                
            } catch (IOException e) {
                System.err.println("Error loading user profile: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return profile;
    }
    
    public void save() {
        Properties props = new Properties();
        props.setProperty("highScore", String.valueOf(highScore));
        props.setProperty("level", String.valueOf(level));
        props.setProperty("totalPlayTime", String.valueOf(totalPlayTime));
        
        try (FileOutputStream out = new FileOutputStream(USER_DIR + File.separator + userName + ".user")) {
            props.store(out, "User Profile for " + userName);
        } catch (IOException e) {
            System.err.println("Error saving user profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void delete() {
        File userFile = new File(USER_DIR + File.separator + userName + ".user");
        if (userFile.exists()) {
            userFile.delete();
        }
    }
    
    // Getters and setters
    public String getUserName() {
        return userName;
    }
    
    public int getHighScore() {
        return highScore;
    }
    
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getTotalPlayTime() {
        return totalPlayTime;
    }
    
    public void addPlayTime(int minutes) {
        this.totalPlayTime += minutes;
    }
    
    @Override
    public String toString() {
        return userName;
    }
}
