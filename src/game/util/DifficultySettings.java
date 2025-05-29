package game.util;

public class DifficultySettings {
    public static class DifficultyConfig {
        private int rocketSpawnInterval; // in milliseconds
        private float rocketSpeedMultiplier;
        private int playerMaxHP;
        private int rocketMaxHP;
        private String description;
        
        public DifficultyConfig(int spawnInterval, float speedMultiplier, int playerHP, int rocketHP, String desc) {
            this.rocketSpawnInterval = spawnInterval;
            this.rocketSpeedMultiplier = speedMultiplier;
            this.playerMaxHP = playerHP;
            this.rocketMaxHP = rocketHP;
            this.description = desc;
        }
        
        public int getRocketSpawnInterval() { return rocketSpawnInterval; }
        public float getRocketSpeedMultiplier() { return rocketSpeedMultiplier; }
        public int getPlayerMaxHP() { return playerMaxHP; }
        public int getRocketMaxHP() { return rocketMaxHP; }
        public String getDescription() { return description; }
    }
    
    public static DifficultyConfig getDifficultyConfig(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return new DifficultyConfig(4000, 1.0f, 100, 20, "Easy Mode");
            case "medium":
                return new DifficultyConfig(3000, 1.25f, 75, 30, "Medium Mode");
            case "hard":
                return new DifficultyConfig(2000, 1.5f, 50, 40, "Hard Mode");
            default:
                return new DifficultyConfig(3000, 1.0f, 75, 30, "Medium Mode");
        }
    }
}
