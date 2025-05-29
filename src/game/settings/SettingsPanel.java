package game.settings;

import game.main.Main;
import game.util.UserProfile;
import game.util.GameSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsPanel extends JPanel {
    private Main mainFrame;
    private Image backgroundImage;
    
    // Settings components
    private JSlider volumeSlider;
    private JSlider brightnessSlider;
    private JComboBox<String> difficultyComboBox;
    private JComboBox<String> userProfileComboBox;
    private JButton createUserButton;
    private JButton deleteUserButton;
    private JButton backButton;
    private JButton saveButton;
    private JTextArea difficultyDescriptionArea;
    
    // Current settings
    private GameSettings gameSettings;
    private List<UserProfile> userProfiles;
    
    public SettingsPanel(Main main) {
        this.mainFrame = main;
        this.gameSettings = GameSettings.getInstance();
        this.userProfiles = loadUserProfiles();
        
        setLayout(new BorderLayout());
        
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/game/image/BG.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading settings background: " + e.getMessage());
        }
        
        // Create main settings container
        JPanel settingsContainer = createSettingsContainer();
        
        // Add to panel with some padding
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(settingsContainer);
        add(centerPanel, BorderLayout.CENTER);
        
        // Add title at the top
        JLabel titleLabel = new JLabel("Game Settings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Add buttons at the bottom
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSettingsContainer() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Volume settings
        JPanel volumePanel = createSettingPanel("Volume");
        volumeSlider = new JSlider(0, 100, gameSettings.getVolume());
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.addChangeListener(e -> {
            gameSettings.setVolume(volumeSlider.getValue());
            if (mainFrame.isGameActive()) {
                mainFrame.applyGameSettings();
            }
        });
        volumePanel.add(volumeSlider);
        container.add(volumePanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Brightness settings
        JPanel brightnessPanel = createSettingPanel("Brightness");
        brightnessSlider = new JSlider(0, 100, gameSettings.getBrightness());
        brightnessSlider.setOpaque(false);
        brightnessSlider.setForeground(Color.WHITE);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        brightnessSlider.setMajorTickSpacing(20);
        brightnessSlider.setMinorTickSpacing(5);
        brightnessPanel.add(brightnessSlider);
        brightnessSlider.addChangeListener(e -> {
            gameSettings.setBrightness(brightnessSlider.getValue());
            if (mainFrame.isGameActive()) {
                mainFrame.applyGameSettings();
            }
        });
        container.add(brightnessPanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Difficulty settings
        JPanel difficultyPanel = createSettingPanel("Difficulty Level");
        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setSelectedItem(gameSettings.getDifficulty());
        difficultyComboBox.addActionListener(e -> updateDifficultyDescription());
        difficultyPanel.add(difficultyComboBox);
        container.add(difficultyPanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // User profile settings
        JPanel userPanel = createSettingPanel("User Profile");
        
        String[] userNames = getUserProfileNames();
        userProfileComboBox = new JComboBox<>(userNames);
        userProfileComboBox.setSelectedItem(gameSettings.getCurrentUser());
        
        JPanel userSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userSelectionPanel.setOpaque(false);
        userSelectionPanel.add(userProfileComboBox);
        
        createUserButton = new JButton("Create New");
        deleteUserButton = new JButton("Delete");
        
        userSelectionPanel.add(createUserButton);
        userSelectionPanel.add(deleteUserButton);
        
        userPanel.add(userSelectionPanel);
        container.add(userPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Difficulty description panel
        JPanel descriptionPanel = createDifficultyDescriptionPanel();
        container.add(descriptionPanel);
        
        // Add action listeners
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewUser();
            }
        });
        
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        // Initialize difficulty description
        updateDifficultyDescription();
        
        return container;
    }
    
    private JPanel createDifficultyDescriptionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            "Difficulty Description", 
            0, 0, 
            new Font("Arial", Font.BOLD, 16), 
            Color.WHITE));
        
        difficultyDescriptionArea = new JTextArea(6, 40);
        difficultyDescriptionArea.setOpaque(false);
        difficultyDescriptionArea.setForeground(Color.WHITE);
        difficultyDescriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        difficultyDescriptionArea.setEditable(false);
        difficultyDescriptionArea.setLineWrap(true);
        difficultyDescriptionArea.setWrapStyleWord(true);
        
        panel.add(difficultyDescriptionArea);
        
        return panel;
    }
    
    private void updateDifficultyDescription() {
        String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
        String description = "";
        
        switch (selectedDifficulty) {
            case "Easy":
                description = "EASY MODE:\n" +
                            "• Rockets spawn every 4 seconds\n" +
                            "• Rockets move at normal speed\n" +
                            "• Player has 100 HP\n" +
                            "• Rockets have 20 HP\n" +
                            "• Perfect for beginners and casual play";
                break;
            case "Medium":
                description = "MEDIUM MODE:\n" +
                            "• Rockets spawn every 3 seconds\n" +
                            "• Rockets move 25% faster\n" +
                            "• Player has 75 HP\n" +
                            "• Rockets have 30 HP\n" +
                            "• Balanced challenge for experienced players";
                break;
            case "Hard":
                description = "HARD MODE:\n" +
                            "• Rockets spawn every 2 seconds\n" +
                            "• Rockets move 50% faster\n" +
                            "• Player has 50 HP\n" +
                            "• Rockets have 40 HP\n" +
                            "• Extreme challenge for expert players only!";
                break;
        }
        
        difficultyDescriptionArea.setText(description);
    }

    public void updateUIFromSettings() {
        volumeSlider.setValue(gameSettings.getVolume());
        brightnessSlider.setValue(gameSettings.getBrightness());
        difficultyComboBox.setSelectedItem(gameSettings.getDifficulty());
        userProfileComboBox.setSelectedItem(gameSettings.getCurrentUser());
        updateDifficultyDescription();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateUIFromSettings();
        }
        super.setVisible(visible);
    }
    
    private JPanel createSettingPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setOpaque(false);
        
        backButton = new JButton("Back");
        saveButton = new JButton("Save Settings");
        
        styleButton(backButton);
        styleButton(saveButton);
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.backFromSettings();
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
                JOptionPane.showMessageDialog(SettingsPanel.this, 
                    "Settings saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        panel.add(backButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void saveSettings() {
        gameSettings.setVolume(volumeSlider.getValue());
        gameSettings.setBrightness(brightnessSlider.getValue());
        gameSettings.setDifficulty((String) difficultyComboBox.getSelectedItem());
        gameSettings.setCurrentUser((String) userProfileComboBox.getSelectedItem());
        gameSettings.saveSettings();
    }
    
    private void createNewUser() {
        String userName = JOptionPane.showInputDialog(this, 
            "Enter new user name:", "Create User", JOptionPane.PLAIN_MESSAGE);
        
        if (userName != null && !userName.trim().isEmpty()) {
            for (UserProfile profile : userProfiles) {
                if (profile.getUserName().equalsIgnoreCase(userName)) {
                    JOptionPane.showMessageDialog(this, 
                        "User already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            UserProfile newProfile = new UserProfile(userName);
            userProfiles.add(newProfile);
            newProfile.save();
            
            userProfileComboBox.addItem(userName);
            userProfileComboBox.setSelectedItem(userName);
        }
    }
    
    private void deleteUser() {
        String selectedUser = (String) userProfileComboBox.getSelectedItem();
        
        if (selectedUser == null || selectedUser.equals("Default")) {
            JOptionPane.showMessageDialog(this, 
                "Cannot delete the default user!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete user '" + selectedUser + "'?\nThis will delete all progress for this user.", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            UserProfile toRemove = null;
            for (UserProfile profile : userProfiles) {
                if (profile.getUserName().equals(selectedUser)) {
                    toRemove = profile;
                    break;
                }
            }
            
            if (toRemove != null) {
                userProfiles.remove(toRemove);
                toRemove.delete();
                
                userProfileComboBox.removeItem(selectedUser);
                userProfileComboBox.setSelectedItem("Default");
            }
        }
    }
    
    private String[] getUserProfileNames() {
        String[] names = new String[userProfiles.size()];
        for (int i = 0; i < userProfiles.size(); i++) {
            names[i] = userProfiles.get(i).getUserName();
        }
        return names;
    }
    
    private List<UserProfile> loadUserProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        
        profiles.add(new UserProfile("Default"));
        
        File userDir = new File("users");
        if (userDir.exists() && userDir.isDirectory()) {
            File[] userFiles = userDir.listFiles();
            if (userFiles != null) {
                for (File file : userFiles) {
                    if (file.isFile() && file.getName().endsWith(".user")) {
                        String userName = file.getName().replace(".user", "");
                        if (!userName.equals("Default")) {
                            profiles.add(UserProfile.load(userName));
                        }
                    }
                }
            }
        }
        
        return profiles;
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

