package Controlador.Projectiles;

import Controlador.Entities.GameEntity;
import Modelo.GameConstants;

public abstract class Projectile extends GameEntity {
    protected double dx;
    protected double dy;
    protected double damage;
    protected boolean active;
    protected ProjectileView view;

    public Projectile(double x, double y, double width, double height, 
                    double speed, double damage) {
        super(x, y, width, height, speed);
        this.damage = damage;
        this.active = true;
    }

    public void calculateDirection(double targetX, double targetY) {
        double diffX = targetX - (x + width/2);
        double diffY = targetY - (y + height/2);
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance > 0) {
            dx = (diffX / distance) * speed;
            dy = (diffY / distance) * speed;
        }
    }

    public void move() {
        if (active) {
            updatePosition(dx, dy);
        }
    }

    public boolean isOutOfBounds() {
        return x < 0 || x > GameConstants.MAP_WIDTH || 
               y < 0 || y > GameConstants.MAP_HEIGHT;
    }

    public double getDamage() { return damage;}
    public double getDx() { return dx;}
    public double getDy() { return dy;}
    public ProjectileView getView() { return view;}
    public void setView(ProjectileView view) { this.view = view;}
  
    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}