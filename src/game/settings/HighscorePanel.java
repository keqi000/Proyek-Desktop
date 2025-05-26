package game.settings;

import game.main.Main;
import game.util.HighscoreManager;
import game.util.HighscoreManager.HighscoreEntry;
import game.util.GameSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class HighscorePanel extends JPanel {
    private Main mainFrame;
    private Image backgroundImage;
    private HighscoreManager highscoreManager;
    private JButton backButton;
    private JButton clearButton;
    
    public HighscorePanel(Main main) {
        this.mainFrame = main;
        this.highscoreManager = HighscoreManager.getInstance();
        
        setLayout(new BorderLayout());
        
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/game/image/BG.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading highscore background: " + e.getMessage());
        }
        
        // Create title
        JLabel titleLabel = new JLabel("HIGH SCORES", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Create highscore content
        JPanel contentPanel = createHighscoreContent();
        add(contentPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHighscoreContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Create header
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel rankLabel = new JLabel("RANK", JLabel.CENTER);
        JLabel nameLabel = new JLabel("PLAYER", JLabel.CENTER);
        JLabel scoreLabel = new JLabel("SCORE", JLabel.CENTER);
        JLabel dateLabel = new JLabel("DATE", JLabel.CENTER);
        
        Font headerFont = new Font("Arial", Font.BOLD, 18);
        Color headerColor = Color.YELLOW;
        
        rankLabel.setFont(headerFont);
        rankLabel.setForeground(headerColor);
        nameLabel.setFont(headerFont);
        nameLabel.setForeground(headerColor);
        scoreLabel.setFont(headerFont);
        scoreLabel.setForeground(headerColor);
        dateLabel.setFont(headerFont);
        dateLabel.setForeground(headerColor);
        
        headerPanel.add(rankLabel);
        headerPanel.add(nameLabel);
        headerPanel.add(scoreLabel);
        headerPanel.add(dateLabel);
        
        panel.add(headerPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.WHITE);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Get highscores and display them
        List<HighscoreEntry> highscores = highscoreManager.getHighscores();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        if (highscores.isEmpty()) {
            JLabel noScoresLabel = new JLabel("No high scores yet!", JLabel.CENTER);
            noScoresLabel.setFont(new Font("Arial", Font.ITALIC, 20));
            noScoresLabel.setForeground(Color.LIGHT_GRAY);
            noScoresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createRigidArea(new Dimension(0, 50)));
            panel.add(noScoresLabel);
        } else {
            String currentUser = GameSettings.getInstance().getCurrentUser();
            
            for (int i = 0; i < highscores.size(); i++) {
                HighscoreEntry entry = highscores.get(i);
                
                JPanel entryPanel = new JPanel(new GridLayout(1, 4, 10, 0));
                entryPanel.setOpaque(false);
                entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                
                JLabel rankEntryLabel = new JLabel(String.valueOf(i + 1), JLabel.CENTER);
                JLabel nameEntryLabel = new JLabel(entry.getPlayerName(), JLabel.CENTER);
                JLabel scoreEntryLabel = new JLabel(String.valueOf(entry.getScore()), JLabel.CENTER);
                JLabel dateEntryLabel = new JLabel(dateFormat.format(entry.getDate()), JLabel.CENTER);
                
                Font entryFont = new Font("Arial", Font.PLAIN, 16);
                Color entryColor = Color.WHITE;
                
                // Highlight current user's entry
                if (entry.getPlayerName().equals(currentUser)) {
                    entryFont = new Font("Arial", Font.BOLD, 16);
                    entryColor = Color.CYAN;
                }
                
                // Special color for top 3
                if (i == 0) {
                    entryColor = new Color(255, 215, 0); // Gold color
                    entryFont = new Font("Arial", Font.BOLD, 18);
                } else if (i == 1) {
                    entryColor = new Color(192, 192, 192); // Silver
                    entryFont = new Font("Arial", Font.BOLD, 17);
                } else if (i == 2) {
                    entryColor = new Color(205, 127, 50); // Bronze
                    entryFont = new Font("Arial", Font.BOLD, 16);
                }
                
                rankEntryLabel.setFont(entryFont);
                rankEntryLabel.setForeground(entryColor);
                nameEntryLabel.setFont(entryFont);
                nameEntryLabel.setForeground(entryColor);
                scoreEntryLabel.setFont(entryFont);
                scoreEntryLabel.setForeground(entryColor);
                dateEntryLabel.setFont(entryFont);
                dateEntryLabel.setForeground(entryColor);
                
                entryPanel.add(rankEntryLabel);
                entryPanel.add(nameEntryLabel);
                entryPanel.add(scoreEntryLabel);
                entryPanel.add(dateEntryLabel);
                
                panel.add(entryPanel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        // Add current user info
        String currentUser = GameSettings.getInstance().getCurrentUser();
        int userBestScore = highscoreManager.getPlayerBestScore(currentUser);
        int userRank = highscoreManager.getPlayerRank(currentUser);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        userInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            "Your Stats", 
            0, 0, 
            new Font("Arial", Font.BOLD, 16), 
            Color.WHITE));
        
        JLabel userLabel = new JLabel("Player: " + currentUser, JLabel.CENTER);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Color.CYAN);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel bestScoreLabel = new JLabel("Best Score: " + userBestScore, JLabel.CENTER);
        bestScoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bestScoreLabel.setForeground(Color.WHITE);
        bestScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel userRankLabel = new JLabel("Rank: " + (userRank > 0 ? "#" + userRank : "Not ranked"), JLabel.CENTER);
        userRankLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userRankLabel.setForeground(Color.WHITE);
        userRankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        userInfoPanel.add(userLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(bestScoreLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(userRankLabel);
        
        panel.add(userInfoPanel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setOpaque(false);
        
        backButton = new JButton("Back");
        clearButton = new JButton("Clear Scores");
        
        styleButton(backButton);
        styleButton(clearButton);
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.backFromHighscore();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    HighscorePanel.this,
                    "Are you sure you want to clear all high scores?\nThis action cannot be undone!",
                    "Clear High Scores",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    clearHighscores();
                }
            }
        });
        
        panel.add(backButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void clearHighscores() {
        // Clear the highscore file
        java.io.File file = new java.io.File("highscores.dat");
        if (file.exists()) {
            file.delete();
        }
        
        // Clear the highscore manager
        highscoreManager.clearAllScores();
        
        // Refresh the panel
        mainFrame.refreshHighscore();
        
        JOptionPane.showMessageDialog(this, 
            "All high scores have been cleared!", 
            "Cleared", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void refreshContent() {
        // Remove old content and recreate
        removeAll();
        
        // Recreate title
        JLabel titleLabel = new JLabel("HIGH SCORES", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Recreate content
        JPanel contentPanel = createHighscoreContent();
        add(contentPanel, BorderLayout.CENTER);
        
        // Recreate buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
            // Apply semi-transparent overlay for better readability
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
