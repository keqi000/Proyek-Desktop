package game.main;

import game.component.PanelGame;
import game.settings.SettingsPanel;
import game.util.GameSettings;

import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame {
    private MainMenuPanel menuPanel;
    private PanelGame gamePanel;
    private SettingsPanel settingsPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameSettings gameSettings;
    private boolean isFullScreen = false;
    private boolean wasInGame = false; // Track if we were in game before settings
    
    public Main() {
        gameSettings = GameSettings.getInstance();
        init();
    }
    
    private void init() {
        setTitle("Java 2D Plane Game");
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create card layout to switch between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create panels
        menuPanel = new MainMenuPanel(this);
        gamePanel = new PanelGame();
        settingsPanel = new SettingsPanel(this);
        
        // Add panels to card layout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(settingsPanel, "SETTINGS");
        
        // Show menu first
        cardLayout.show(mainPanel, "MENU");
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add key listener for fullscreen toggle (F11) and menu navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    toggleFullScreen();
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    // M key is used as a special key to return to main menu from game
                    showMainMenu();
                }
            }
        });
        
        // Make sure the frame can receive key events
        setFocusable(true);
        requestFocus();
        
        // Add window listener for game panel
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Game will start when switched to game panel
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                // Save settings when closing
                gameSettings.saveSettings();
            }
        });
    }

    public boolean isGameActive() {
    return gamePanel != null && gamePanel.isShowing();
}

// Add method to apply settings to the game
public void applyGameSettings() {
    if (gamePanel != null) {
        gamePanel.applySettings();
    }
}
    
    public void toggleFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if (!isFullScreen) {
            // Switch to fullscreen
            if (gd.isFullScreenSupported()) {
                dispose(); // Dispose the current frame
                setUndecorated(true); // Remove window decorations
                setResizable(false);
                gd.setFullScreenWindow(this);
                isFullScreen = true;
            }
        } else {
            // Switch back to windowed mode
            gd.setFullScreenWindow(null);
            dispose(); // Dispose the current frame
            setUndecorated(false); // Restore window decorations
            setResizable(true);
            setSize(1366, 768);
            setLocationRelativeTo(null);
            setVisible(true);
            isFullScreen = false;
        }
        
        // Make sure the frame can receive key events after toggling
        setFocusable(true);
        requestFocus();
    }
    
    public void startGame() {
    // Remove the old game panel if it exists
    if (gamePanel != null) {
        mainPanel.remove(gamePanel);
    }
    
    // Create a new game panel
    gamePanel = new PanelGame();
    mainPanel.add(gamePanel, "GAME");
    
    // Show the game panel
    cardLayout.show(mainPanel, "GAME");
    gamePanel.start();
    
    // Make sure the game panel gets focus
    gamePanel.requestFocusInWindow();
}

    
    public void resumeGame() {
        cardLayout.show(mainPanel, "GAME");
        gamePanel.resume();
    }
    
    // Update the openSettings method
    public void openSettings() {
        // Remember if we were in game
        wasInGame = gamePanel != null && gamePanel.isShowing();
        
        // Update settings panel UI with current settings
        settingsPanel.updateUIFromSettings();
        
        // Show the settings panel
        cardLayout.show(mainPanel, "SETTINGS");
    }

    
    // Fixed version
    public void showMainMenu() {
    // Completely stop the game
    if (gamePanel != null) {
        gamePanel.stopGame(); // We'll need to add this method to PanelGame
    }
    
    // Show the menu panel
    cardLayout.show(mainPanel, "MENU");
    menuPanel.setVisible(true);
    menuPanel.requestFocusInWindow();
    
    // Force a repaint
    mainPanel.revalidate();
    mainPanel.repaint();
    }


    public void backFromSettings() {
        // Return to the previous screen (game or menu)
        if (wasInGame) {
            resumeGame();
            wasInGame = false;
        } else {
            // If we were in main menu before settings
            showMainMenu();
        }
    }
    
    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
