package game.obj;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

public class Rocket extends HpRender {

    public Rocket() {
        super(new HP(20, 20));
        this.image = new ImageIcon(getClass().getResource("/game/image/rocket.png")).getImage();
        Path2D p = new Path2D.Double();
        p.moveTo(0, ROCKET_SIZE / 2);
        p.lineTo(15, 10);
        p.lineTo(ROCKET_SIZE - 5, 13);
        p.lineTo(ROCKET_SIZE + 10, ROCKET_SIZE / 2);
        p.lineTo(ROCKET_SIZE - 5, ROCKET_SIZE - 13);
        p.lineTo(15, ROCKET_SIZE - 10);
        rocketShap = new Area(p);
        this.maxHP = 20; // Default HP
        this.speedMultiplier = 1.0f; // Default speed
    }

    public static final double ROCKET_SIZE = 50;
    private double x;
    private double y;
    private final float speed = 0.3f;
    private float angle = 0;
    private final Image image;
    private final Area rocketShap;
    private double maxHP = 20;
    private float speedMultiplier = 1.0f;

    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed * speedMultiplier;
        y += Math.sin(Math.toRadians(angle)) * speed * speedMultiplier;
    }

    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), ROCKET_SIZE / 2, ROCKET_SIZE / 2);
        g2.drawImage(image, tran, null);
        Shape shap = getShape();
        hpRender(g2, shap, y);
        g2.setTransform(oldTransform);

        //  Test
        // g2.setColor(new Color(36, 214, 63));
        // g2.draw(shap);
        // g2.draw(shap.getBounds2D());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), ROCKET_SIZE / 2, ROCKET_SIZE / 2);
        return new Area(afx.createTransformedShape(rocketShap));
    }

    public boolean check(int width, int height) {
        Rectangle size = getShape().getBounds();
        if (x <= -size.getWidth() || y < -size.getHeight() || x > width || y > height) {
            return false;
        } else {
            return true;
        }
    }
    
    // New methods for difficulty support
    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = multiplier;
    }
    
    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
        // Update the HP object
        this.hp = new HP(maxHP, maxHP);
    }
    
    public double getMaxHP() {
        return maxHP;
    }
    
    public double getHP() {
        return hp.getCurrentHp();
    }
    
    public boolean updateHP(double damage) {
        // Use the parent class method instead of calling hp.updateHP directly
        return super.updateHP(damage);
    }
}
