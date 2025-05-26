package game.component;

import game.main.Main;
import game.obj.Bullet;
import game.obj.Effect;
import game.obj.Player;
import game.obj.Rocket;
import game.obj.sound.Sound;
import game.util.GameSettings;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class PanelGame extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread thread;
    private boolean start = true;
    private boolean paused = false;
    private Key key;
    private int shotTime;
    private GameSettings gameSettings;

    //  Game FPS
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;
    //  Game Object
    private Sound sound;
    private Player player;
    private List<Bullet> bullets;
    private List<Rocket> rockets;
    private List<Effect> boomEffects;
    private int score = 0;

    public PanelGame() {
        gameSettings = GameSettings.getInstance();
        
        // Add mouse listener for button clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (paused) {
                    int x = e.getX();
                    int y = e.getY();
                    
                    // Check if resume button is clicked
                    if (isButtonClicked(x, y, width / 2, 380)) {
                        togglePause();
                    }
                    // Check if restart button is clicked
                    else if (isButtonClicked(x, y, width / 2, 430)) {
                        resetGame();
                        togglePause();
                    }
                    // Check if main menu button is clicked
                    else if (isButtonClicked(x, y, width / 2, 480)) {
                    // Stop the game completely
                    start = false; // Stop all game threads
                    paused = true; // Ensure game is paused
                    
                    // Get the Main frame instance
                    Main mainFrame = (Main)SwingUtilities.getWindowAncestor(PanelGame.this);
                    
                    // Use SwingUtilities.invokeLater to ensure UI updates happen on EDT
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            mainFrame.showMainMenu();
                        }
                    });
                }

                  
                    // Check if volume slider is clicked
                    if (y >= 240 && y <= 260) {
                        int sliderX = (width - 300) / 2;
                        if (x >= sliderX && x <= sliderX + 300) {
                            int newVolume = (int)(((x - sliderX) / 300.0) * 100);
                            gameSettings.setVolume(newVolume);
                            gameSettings.saveSettings();
                            applySettings(); // Apply the new settings immediately
                        }
                    }
                    
                    // Check if brightness slider is clicked
                    if (y >= 310 && y <= 330) {
                        int sliderX = (width - 300) / 2;
                        if (x >= sliderX && x <= sliderX + 300) {
                            int newBrightness = (int)(((x - sliderX) / 300.0) * 100);
                            gameSettings.setBrightness(newBrightness);
                            gameSettings.saveSettings();
                            applySettings(); // Apply the new settings immediately
                        }
                    }
                }
            }
        });
    }

    // Add this method to apply settings
    public void applySettings() {
        // Apply volume setting
        if (sound != null) {
            sound.setVolume(gameSettings.getVolume());
        }
        
        // Apply brightness setting - we'll use this when drawing
        repaint();
    }

    public void start() {
        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();
                    drawBackground();
                    drawGame();
                    render();
                    long time = System.nanoTime() - startTime;
                    if (time < TARGET_TIME) {
                        long sleep = (TARGET_TIME - time) / 1000000;
                        sleep(sleep);
                    }
                }
            }
        });
        initObjectGame();
        initKeyboard();
        initBullets();
        
        // Apply settings at the start
        applySettings();
        
        thread.start();
    }

    private void addRocket() {
        Random ran = new Random();
        int locationY = ran.nextInt(height - 50) + 25;
        Rocket rocket = new Rocket();
        rocket.changeLocation(0, locationY);
        rocket.changeAngle(0);
        rockets.add(rocket);
        int locationY2 = ran.nextInt(height - 50) + 25;
        Rocket rocket2 = new Rocket();
        rocket2.changeLocation(width, locationY2);
        rocket2.changeAngle(180);
        rockets.add(rocket2);
    }

    private void initObjectGame() {
        sound = new Sound();
        player = new Player();
        player.changeLocation(150, 150);
        rockets = new ArrayList<>();
        boomEffects = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    if (!paused) {
                        addRocket();
                    }
                    sleep(3000);
                }
            }
        }).start();
    }

    private void resetGame() {
        score = 0;
        rockets.clear();
        bullets.clear();
        player.changeLocation(150, 150);
        player.reset();
    }

    public void stopGame() {
    // Set paused to true to stop all game updates
    paused = true;
    
    // Force a repaint to ensure the game is no longer rendering
    repaint();
    }


    private void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(true);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(true);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(true);
                } else if (e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(true);
                } else if (e.getKeyCode() == KeyEvent.VK_K) {
                    key.setKey_k(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(true);
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePause();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && paused) {
                    // Return to main menu when ESC is pressed during pause
                    paused = false;
                    getParent().getParent().dispatchEvent(
                        new KeyEvent(getParent().getParent(), KeyEvent.KEY_PRESSED, 
                            System.currentTimeMillis(), 0, KeyEvent.VK_M, KeyEvent.CHAR_UNDEFINED)
                    );
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(false);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(false);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(false);
                } else if (e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(false);
                } else if (e.getKeyCode() == KeyEvent.VK_K) {
                    key.setKey_k(false);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(false);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f;
                while (start) {
                    if (!paused && player.isAlive()) {
                        float angle = player.getAngle();
                        if (key.isKey_left()) {
                            angle -= s;
                        }
                        if (key.isKey_right()) {
                            angle += s;
                        }
                        if (key.isKey_j() || key.isKey_k()) {
                            if (shotTime == 0) {
                                if (key.isKey_j()) {
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 5, 3f));
                                } else {
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 20, 3f));
                                }
                                sound.soundShoot();
                            }
                            shotTime++;
                            if (shotTime == 15) {
                                shotTime = 0;
                            }
                        } else {
                            shotTime = 0;
                        }
                        if (key.isKey_space()) {
                            player.speedUp();
                        } else {
                            player.speedDown();
                        }
                        player.update();
                        player.changeAngle(angle);
                    } else if (!paused) {
                        if (key.isKey_enter()) {
                            resetGame();
                        }
                    }
                    for (int i = 0; i < rockets.size(); i++) {
                        Rocket rocket = rockets.get(i);
                        if (rocket != null && !paused) {
                            rocket.update();
                            if (!rocket.check(width, height)) {
                                rockets.remove(rocket);
                            } else {
                                if (player.isAlive()) {
                                    checkPlayer(rocket);
                                }
                            }
                        }
                    }
                    sleep(5);
                }
            }
        }).start();
    }

    private void initBullets() {
        bullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    if (!paused) {
                        for (int i = 0; i < bullets.size(); i++) {
                            Bullet bullet = bullets.get(i);
                            if (bullet != null) {
                                bullet.update();
                                checkBullets(bullet);
                                if (!bullet.check(width, height)) {
                                    bullets.remove(bullet);
                                }
                            } else {
                                bullets.remove(bullet);
                            }
                        }
                        for (int i = 0; i < boomEffects.size(); i++) {
                            Effect boomEffect = boomEffects.get(i);
                            if (boomEffect != null) {
                                boomEffect.update();
                                if (!boomEffect.check()) {
                                    boomEffects.remove(boomEffect);
                                }
                            } else {
                                boomEffects.remove(boomEffect);
                            }
                        }
                    }
                    sleep(1);
                }
            }
        }).start();
    }

    private void checkBullets(Bullet bullet) {
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                Area area = new Area(bullet.getShape());
                area.intersect(rocket.getShape());
                if (!area.isEmpty()) {
                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(230, 207, 105)));
                    if (!rocket.updateHP(bullet.getSize())) {
                        score++;
                        rockets.remove(rocket);
                        sound.soundDestroy();
                        double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                        double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    } else {
                        sound.soundHit();
                    }
                    bullets.remove(bullet);
                }
            }
        }
    }

    private void checkPlayer(Rocket rocket) {
        if (rocket != null) {
            Area area = new Area(player.getShape());
            area.intersect(rocket.getShape());
            if (!area.isEmpty()) {
                double rocketHp = rocket.getHP();
                if (!rocket.updateHP(player.getHP())) {
                    rockets.remove(rocket);
                    sound.soundDestroy();
                    double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                    double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                }
                if (!player.updateHP(rocketHp)) {
                    player.setAlive(false);
                    sound.soundDestroy();
                    double x = player.getX() + Player.PLAYER_SIZE / 2;
                    double y = player.getY() + Player.PLAYER_SIZE / 2;
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                }
            }
        }
    }

    private void drawBackground() {
        // Apply brightness to background
        int brightness = gameSettings.getBrightness();
        float brightnessRatio = brightness / 100f;
        
        // Adjust background color based on brightness
        int colorValue = Math.max(5, Math.min(50, (int)(30 * brightnessRatio)));
        g2.setColor(new Color(colorValue, colorValue, colorValue));
        g2.fillRect(0, 0, width, height);
    }

    private void drawGame() {
        if (player.isAlive()) {
            player.draw(g2);
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet != null) {
                bullet.draw(g2);
            }
        }
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                rocket.draw(g2);
            }
        }
        for (int i = 0; i < boomEffects.size(); i++) {
            Effect boomEffect = boomEffects.get(i);
            if (boomEffect != null) {
                boomEffect.draw(g2);
            }
        }
        
        // Apply brightness filter if needed
        int brightness = gameSettings.getBrightness();
        float brightnessRatio = brightness / 100f;
        
        // Create a semi-transparent overlay for brightness
        if (brightnessRatio < 0.7f) {
            // Darken the screen for low brightness
            g2.setColor(new Color(0, 0, 0, (int)(255 * (0.7f - brightnessRatio))));
            g2.fillRect(0, 0, width, height);
        } else if (brightnessRatio > 1.0f) {
            // Lighten the screen for high brightness
            g2.setColor(new Color(255, 255, 255, (int)(100 * (brightnessRatio - 1.0f))));
            g2.fillRect(0, 0, width, height);
        }
        
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
        g2.drawString("Score : " + score, 10, 20);
        
        // Draw pause hint
        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
        g2.drawString("Press 'P' to pause", width - 120, 20);
        
        // Draw pause menu directly on the game canvas if paused
        if (paused) {
            // Draw semi-transparent overlay
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, width, height);
            
            // Draw pause title
            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD, 36f));
            String pauseText = "PAUSED";
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(pauseText, g2);
            double textWidth = r2.getWidth();
            double x = (width - textWidth) / 2;
            g2.drawString(pauseText, (int) x, 150);
            
            // Draw volume label and value
            g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
            String volumeLabel = "Volume: " + gameSettings.getVolume() + "%";
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(volumeLabel, g2);
            textWidth = r2.getWidth();
            x = (width - textWidth) / 2;
            g2.drawString(volumeLabel, (int) x, 220);
            
            // Draw volume slider representation
            int sliderWidth = 300;
            int sliderHeight = 20;
            int sliderX = (width - sliderWidth) / 2;
            int sliderY = 240;
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(sliderX, sliderY, sliderWidth, sliderHeight);
            g2.setColor(Color.GREEN);
            int fillWidth = (int)(sliderWidth * (gameSettings.getVolume() / 100.0));
            g2.fillRect(sliderX, sliderY, fillWidth, sliderHeight);
            g2.setColor(Color.WHITE);
            g2.drawRect(sliderX, sliderY, sliderWidth, sliderHeight);
            
            // Draw brightness label and value
            g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
            String brightnessLabel = "Brightness: " + gameSettings.getBrightness() + "%";
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(brightnessLabel, g2);
            textWidth = r2.getWidth();
            x = (width - textWidth) / 2;
            g2.drawString(brightnessLabel, (int) x, 290);
            
            // Draw brightness slider representation
            sliderY = 310;
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(sliderX, sliderY, sliderWidth, sliderHeight);
            g2.setColor(Color.YELLOW);
            fillWidth = (int)(sliderWidth * (gameSettings.getBrightness() / 100.0));
            g2.fillRect(sliderX, sliderY, fillWidth, sliderHeight);
            g2.setColor(Color.WHITE);
            g2.drawRect(sliderX, sliderY, sliderWidth, sliderHeight);
            
            // Draw buttons
            drawButton(g2, "Resume Game", width / 2, 380);
            drawButton(g2, "Restart Game", width / 2, 430);
            drawButton(g2, "Main Menu", width / 2, 480);
            
            // Draw instructions
            g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
            String instructions = "Click on buttons or sliders to interact";
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(instructions, g2);
            textWidth = r2.getWidth();
            x = (width - textWidth) / 2;
            g2.drawString(instructions, (int) x, 540);
            
            String instructions2 = "Press ESC to return to main menu";
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(instructions2, g2);
            textWidth = r2.getWidth();
            x = (width - textWidth) / 2;
            g2.drawString(instructions2, (int) x, 560);
        }
        
        if (!player.isAlive()) {
            String text = "GAME OVER";
            String textKey = "Press key enter to Continue ...";
            g2.setFont(getFont().deriveFont(Font.BOLD, 50f));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(text, g2);
            double textWidth = r2.getWidth();
            double textHeight = r2.getHeight();
            double x = (width - textWidth) / 2;
            double y = (height - textHeight) / 2;
            g2.drawString(text, (int) x, (int) y + fm.getAscent());
            g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(textKey, g2);
            textWidth = r2.getWidth();
            textHeight = r2.getHeight();
            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(textKey, (int) x, (int) y + fm.getAscent() + 50);
        }
    }
    
    private void drawButton(Graphics2D g2, String text, int centerX, int y) {
        int buttonWidth = 200;
        int buttonHeight = 40;
        int x = centerX - buttonWidth / 2;
        
        // Draw button background
        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(x, y, buttonWidth, buttonHeight);
        
        // Draw button border
        g2.setColor(new Color(120, 120, 120));
        g2.drawRect(x, y, buttonWidth, buttonHeight);
        
        // Draw button text
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r2 = fm.getStringBounds(text, g2);
        double textWidth = r2.getWidth();
        double textHeight = r2.getHeight();
        double textX = centerX - textWidth / 2;
        double textY = y + (buttonHeight - textHeight) / 2 + fm.getAscent();
        g2.drawString(text, (int) textX, (int) textY);
    }

    private void render() {
        Graphics g = getGraphics();
        if (g != null) {
            g.drawImage(image, 0, 0, null);
            g.dispose();
        }
    }

    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
    
    public void togglePause() {
        paused = !paused;
        
        if (!paused) {
            // When unpausing, make sure the game panel gets focus
            requestFocusInWindow();
        }
    }
    
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void resume() {
        paused = false;
        requestFocusInWindow(); // Make sure the game panel gets focus back
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        this.width = width;
        this.height = height;
    }
    
    private boolean isButtonClicked(int mouseX, int mouseY, int buttonCenterX, int buttonY) {
        int buttonWidth = 200;
        int buttonHeight = 40;
        int buttonX = buttonCenterX - buttonWidth / 2;
        
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }
}
