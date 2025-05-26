package game.util;

public class DifficultySettings {
    
    public static class DifficultyConfig {
        private int playerMaxHP;
        private int rocketMaxHP;
        private float rocketSpeedMultiplier;
        private int rocketSpawnInterval;
        
        public DifficultyConfig(int playerMaxHP, int rocketMaxHP, float rocketSpeedMultiplier, int rocketSpawnInterval) {
            this.playerMaxHP = playerMaxHP;
            this.rocketMaxHP = rocketMaxHP;
            this.rocketSpeedMultiplier = rocketSpeedMultiplier;
            this.rocketSpawnInterval = rocketSpawnInterval;
        }
        
        public int getPlayerMaxHP() {
            return playerMaxHP;
        }
        
        public int getRocketMaxHP() {
            return rocketMaxHP;
        }
        
        public float getRocketSpeedMultiplier() {
            return rocketSpeedMultiplier;
        }
        
        public int getRocketSpawnInterval() {
            return rocketSpawnInterval;
        }
    }
    
    public static DifficultyConfig getDifficultyConfig(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return new DifficultyConfig(75, 15, 0.8f, 4000); // More HP, weaker enemies, slower spawn
            case "medium":
                return new DifficultyConfig(50, 20, 1.0f, 3000); // Default values
            case "hard":
                return new DifficultyConfig(30, 30, 1.5f, 2000); // Less HP, stronger enemies, faster spawn
            default:
                return new DifficultyConfig(50, 20, 1.0f, 3000); // Default to medium
        }
    }
}
