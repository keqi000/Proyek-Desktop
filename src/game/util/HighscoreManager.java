package game.util;

import java.io.*;
import java.util.*;

public class HighscoreManager {
    private static HighscoreManager instance;
    private List<HighscoreEntry> highscores;
    private final String HIGHSCORE_FILE = "highscores.dat";
    private final int MAX_ENTRIES = 10;
    
    private HighscoreManager() {
        highscores = new ArrayList<>();
        loadHighscores();
    }
    
    public static synchronized HighscoreManager getInstance() {
        if (instance == null) {
            instance = new HighscoreManager();
        }
        return instance;
    }
    
    public void addScore(String playerName, int score) {
        // Check if player already exists
        HighscoreEntry existingEntry = null;
        for (HighscoreEntry entry : highscores) {
            if (entry.getPlayerName().equals(playerName)) {
                existingEntry = entry;
                break;
            }
        }
        
        // If player exists and new score is higher, update it
        if (existingEntry != null) {
            if (score > existingEntry.getScore()) {
                existingEntry.setScore(score);
                existingEntry.setDate(new Date());
            }
        } else {
            // Add new entry
            highscores.add(new HighscoreEntry(playerName, score, new Date()));
        }
        
        // Sort by score (descending)
        Collections.sort(highscores, new Comparator<HighscoreEntry>() {
            @Override
            public int compare(HighscoreEntry a, HighscoreEntry b) {
                return Integer.compare(b.getScore(), a.getScore());
            }
        });
        
        // Keep only top entries
        if (highscores.size() > MAX_ENTRIES) {
            highscores = highscores.subList(0, MAX_ENTRIES);
        }
        
        saveHighscores();
    }
    
    public List<HighscoreEntry> getHighscores() {
        return new ArrayList<>(highscores);
    }
    
    public int getPlayerBestScore(String playerName) {
        for (HighscoreEntry entry : highscores) {
            if (entry.getPlayerName().equals(playerName)) {
                return entry.getScore();
            }
        }
        return 0;
    }
    
    public int getPlayerRank(String playerName) {
        for (int i = 0; i < highscores.size(); i++) {
            if (highscores.get(i).getPlayerName().equals(playerName)) {
                return i + 1;
            }
        }
        return -1; // Not in top list
    }
    
    public void clearAllScores() {
        highscores.clear();
        saveHighscores();
        
        // Also delete the file
        File file = new File(HIGHSCORE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    private void loadHighscores() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<HighscoreEntry> loadedScores = (List<HighscoreEntry>) obj;
                highscores = loadedScores;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading highscores: " + e.getMessage());
            highscores = new ArrayList<>();
        }
    }
    
    private void saveHighscores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE))) {
            oos.writeObject(highscores);
        } catch (IOException e) {
            System.err.println("Error saving highscores: " + e.getMessage());
        }
    }
    
    public static class HighscoreEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        private String playerName;
        private int score;
        private Date date;
        
        public HighscoreEntry(String playerName, int score, Date date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public int getScore() {
            return score;
        }
        
        public void setScore(int score) {
            this.score = score;
        }
        
        public Date getDate() {
            return date;
        }
        
        public void setDate(Date date) {
            this.date = date;
        }
        
        @Override
        public String toString() {
            return playerName + " - " + score;
        }
    }
}
