package game.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {
    private Image backgroundImage;
    private JButton startButton;
    private JButton settingsButton;
    private JButton highscoreButton;
    private JButton exitButton;
    private Main mainFrame;
    
    public MainMenuPanel(Main main) {
        this.mainFrame = main;
        setLayout(new BorderLayout());
        
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/game/image/BG.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading menu background: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Create top panel for settings and highscore buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        // Create settings button for top-right corner
        settingsButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/game/image/Settings_BTN.png"));
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            settingsButton.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.err.println("Error loading settings button image: " + e.getMessage());
        }
        
        // Create highscore button for top-left corner
        highscoreButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/game/image/Rating_BTN.png"));
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            highscoreButton.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.err.println("Error loading highscore button image: " + e.getMessage());
            // Fallback text if image not found
            highscoreButton.setText("HIGH");
            highscoreButton.setFont(new Font("Arial", Font.BOLD, 10));
            highscoreButton.setForeground(Color.WHITE);
        }
        
        // Style both top buttons
        styleTopButton(settingsButton);
        styleTopButton(highscoreButton);
        
        // Add action listeners
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.openSettings();
            }
        });
        
        highscoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.openHighscore();
            }
        });
        
        // Create panels for top buttons
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);
        topLeftPanel.add(highscoreButton);
        
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.add(settingsButton);
        
        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Create a panel for main buttons (start and exit)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        
        // Create start button
        startButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/game/image/Start_BTN.png"));
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(300, 90, Image.SCALE_SMOOTH);
            startButton.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.err.println("Error loading start button image: " + e.getMessage());
        }
        
        // Create exit button
        exitButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/game/image/Exit_BTN.png"));
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(300, 90, Image.SCALE_SMOOTH);
            exitButton.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.err.println("Error loading exit button image: " + e.getMessage());
        }
        
        // Style main buttons
        styleButton(startButton);
        styleButton(exitButton);
        
        // Add action listeners
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.startGame();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Add buttons to panel with spacing between them
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        buttonPanel.add(exitButton);
        
        // Center align main buttons
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create a wrapper panel to center the button panel vertically
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(buttonPanel);
        
        add(wrapperPanel, BorderLayout.CENTER);
    }
    
    private void styleTopButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(60, 60));
    }
    
    private void styleButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Dimension buttonSize = new Dimension(300, 90);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("PLANE GAME", getWidth() / 2 - 120, 100);
        }
    }
}
