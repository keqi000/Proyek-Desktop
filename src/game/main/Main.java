package game.main;

import game.component.PanelGame;
import game.settings.SettingsPanel;
import game.settings.HighscorePanel;
import game.util.GameSettings;
import game.util.HighscoreManager;

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
    private HighscorePanel highscorePanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameSettings gameSettings;
    private HighscoreManager highscoreManager;
    private boolean isFullScreen = false;
    private boolean wasInGame = false;
    
    public Main() {
        gameSettings = GameSettings.getInstance();
        highscoreManager = HighscoreManager.getInstance();
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
        highscorePanel = new HighscorePanel(this);
        
        // Add panels to card layout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(settingsPanel, "SETTINGS");
        mainPanel.add(highscorePanel, "HIGHSCORE");
        
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
                    showMainMenu();
                }
            }
        });
        
        setFocusable(true);
        requestFocus();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Game will start when switched to game panel
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                gameSettings.saveSettings();
            }
        });
    }

    public boolean isGameActive() {
        return gamePanel != null && gamePanel.isShowing();
    }

    public void applyGameSettings() {
        if (gamePanel != null) {
            gamePanel.applySettings();
        }
    }
    
    public void toggleFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if (!isFullScreen) {
            if (gd.isFullScreenSupported()) {
                dispose();
                setUndecorated(true);
                setResizable(false);
                gd.setFullScreenWindow(this);
                isFullScreen = true;
            }
        } else {
            gd.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setResizable(true);
            setSize(1366, 768);
            setLocationRelativeTo(null);
            setVisible(true);
            isFullScreen = false;
        }
        
        setFocusable(true);
        requestFocus();
    }
    
    public void startGame() {
        if (gamePanel != null) {
            mainPanel.remove(gamePanel);
        }
        
        gamePanel = new PanelGame();
        mainPanel.add(gamePanel, "GAME");
        
        cardLayout.show(mainPanel, "GAME");
        gamePanel.start();
        
        gamePanel.requestFocusInWindow();
    }

    public void resumeGame() {
        cardLayout.show(mainPanel, "GAME");
        gamePanel.resume();
    }
    
    public void openSettings() {
        wasInGame = gamePanel != null && gamePanel.isShowing();
        settingsPanel.updateUIFromSettings();
        cardLayout.show(mainPanel, "SETTINGS");
    }

    public void openHighscore() {
        wasInGame = gamePanel != null && gamePanel.isShowing();
        highscorePanel.refreshContent();
        cardLayout.show(mainPanel, "HIGHSCORE");
    }

    public void showMainMenu() {
        if (gamePanel != null) {
            gamePanel.stopGame();
        }
        
        cardLayout.show(mainPanel, "MENU");
        menuPanel.setVisible(true);
        menuPanel.requestFocusInWindow();
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void backFromSettings() {
        if (wasInGame) {
            resumeGame();
            wasInGame = false;
        } else {
            showMainMenu();
        }
    }
    
    public void backFromHighscore() {
        if (wasInGame) {
            resumeGame();
            wasInGame = false;
        } else {
            showMainMenu();
        }
    }
    
    public void refreshHighscore() {
        // Reload highscore manager and refresh panel
        highscoreManager = HighscoreManager.getInstance();
        highscorePanel.refreshContent();
    }
    
    // Method to save score when game ends
    public void saveGameScore(int score) {
        String currentUser = gameSettings.getCurrentUser();
        highscoreManager.addScore(currentUser, score);
    }
    
    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
