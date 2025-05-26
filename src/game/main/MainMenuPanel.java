package game.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {
    private Image backgroundImage;
    private JButton startButton;
    private JButton settingsButton;
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
        
        // Create settings button separately for top-right corner
        settingsButton = new JButton();
        try {
            // Load the image and scale it to 90x90
                        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/game/image/Settings_BTN.png"));
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            settingsButton.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.err.println("Error loading settings button image: " + e.getMessage());
        }
        
        // Style settings button
        settingsButton.setContentAreaFilled(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setPreferredSize(new Dimension(90, 90));
        
        // Add action listener to settings button
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.openSettings();
            }
        });
        
        // Create a panel for the settings button in the top-right corner
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.add(settingsButton);
        add(topRightPanel, BorderLayout.NORTH);
        
        // Create a panel for main buttons (start and exit)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        
        // Create start button
        startButton = new JButton();
        try {
            // Load the image and scale it to the desired button size
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
            // Load the image and scale it to the desired button size
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
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 25))); // Spacing between buttons
        buttonPanel.add(exitButton);
        
        // Center align main buttons
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create a wrapper panel to center the button panel vertically
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(buttonPanel);
        
        // Add wrapper panel to the center of the main panel
        add(wrapperPanel, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set preferred size for main buttons
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
            // Fallback if image fails to load
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("PLANE GAME", getWidth() / 2 - 120, 100);
        }
    }
}

