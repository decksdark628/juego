package Controlador.Entities;

import Modelo.GameConstants;

public abstract class GameEntity {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double speed;
    
    public GameEntity(double x, double y, double width, double height, double speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void updatePosition(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }
    
    public boolean collidesWith(GameEntity other) {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }
    
    public boolean isOutOfBounds() {
        return x < 0 || x + width > GameConstants.MAP_WIDTH ||
               y < 0 || y + height > GameConstants.MAP_HEIGHT;
    }
}