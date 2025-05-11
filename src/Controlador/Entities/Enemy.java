package Controlador.Entities;

import Controlador.Projectiles.EnemyProjectile;
import Modelo.GameConstants;

import java.util.List;



public abstract class Enemy extends GameEntity {
    protected int hp;
    protected int damage;
    protected int points;
    protected long lastDamageTime;
    protected long lastShotTime;
    protected double minShootDistance;
    protected double maxShootDistance;
    protected double shootInterval;
    protected List<EnemyProjectile> enemyProjectiles;
    protected EnemySprite enemyView;

    public Enemy(double x, double y, double width, double height, double speed,
                int hp, int damage, int points) {
        super(x, y, width, height, speed);
        this.hp = hp;
        this.damage = damage;
        this.points = points;
    }
    
    protected void moveTowardsPlayer(double playerX, double playerY, List<Enemy> allEnemies, double deltaTime) {
        double[] direction = calculateDirection(playerX, playerY, deltaTime);
        moveInDirection(direction[0], direction[1]);
    }

    private double[] calculateDirection(double targetX, double targetY, double deltaTime) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > 0) {
            dx = (dx / distance) * speed;
            dy = (dy / distance) * speed;

            dx *= deltaTime * 60.0; 
            dy *= deltaTime * 60.0;
        }
        return new double[]{dx, dy};
    }

    protected void avoidOverlap(List<Enemy> allEnemies, double deltaTime) {
        for (Enemy other : allEnemies) {
            if (other != this) {
                double dx = (x + width/2) - (other.x + other.width/2);
                double dy = (y + height/2) - (other.y + other.height/2);
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                double minDistance = (width + other.width) / 2;
                if (distance < minDistance && distance > 0) {
                    dx = dx / distance;
                    dy = dy / distance;
                    
                    double separationForce = (minDistance - distance) / minDistance * speed * 0.5;
                    
                    double pushX = dx * separationForce * deltaTime * 60.0;
                    double pushY = dy * separationForce * deltaTime * 60.0;
                    
                    updatePosition(pushX, pushY);
                }
            }
        }
    }
    
    private void moveInDirection(double dx, double dy) {
        updatePosition(dx, dy);
    }

    public void hit(int damage) {
        reduceHp(damage);
        ensureHpIsNonNegative();
    }

    private void reduceHp(int damage) {
        hp -= damage;
    }

    private void ensureHpIsNonNegative() {
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public boolean canDamagePlayer() {
        if (isDamageCooldownOver()) {
            updateLastDamageTime();
            return true;
        }
        return false;
    }

    private boolean isDamageCooldownOver() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastDamageTime >= GameConstants.DAMAGE_INTERVAL;
    }

    private void updateLastDamageTime() {
        lastDamageTime = System.currentTimeMillis();
    }

    public boolean isInShootingRange(double playerX, double playerY) {
        double distance = calculateDistance(playerX, playerY);
        return isWithinShootingRange(distance);
    }

    private boolean isWithinShootingRange(double distance) {
        return distance >= minShootDistance && distance <= maxShootDistance;
    }

    public boolean canShoot() {
        return isShootCooldownOver();
    }

    private boolean isShootCooldownOver() {
        return System.currentTimeMillis() - lastShotTime >= shootInterval;
    }

    protected double calculateDistance(double targetX, double targetY) {
        double dx = calculateHorizontalDistance(targetX);
        double dy = calculateVerticalDistance(targetY);
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double calculateHorizontalDistance(double targetX) {
        return (x + width / 2) - targetX;
    }

    private double calculateVerticalDistance(double targetY) {
        return (y + height / 2) - targetY;
    }

    public boolean update(double playerX, double playerY, Player player, List<Enemy> allEnemies, double deltaTime) {
        if (isDead()) {
            return false; 
        }

        updateMovement(playerX, playerY, allEnemies, deltaTime);
        updateAttacks(playerX, playerY, player, allEnemies, deltaTime);
        avoidOverlap(allEnemies, deltaTime);
        return true; 
    }

    public abstract void updateMovement(double playerX, double playerY, List<Enemy> allEnemies, double deltaTime);
    public abstract void updateAttacks(double playerX, double playerY, Player player, List<Enemy> allEnemies, double deltaTime);

    public void setHp(int hp) { this.hp=hp; }
    public void setEnemyView(EnemySprite enemyView) { this.enemyView = enemyView; }
    public void setPoints(int points) { this.points = points; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setShootInterval(double shootInterval) { this.shootInterval = shootInterval; }
    
    public double getShootInterval() { return shootInterval; }
    public int getHp() { return hp; }
    public int getDamage() { return damage; }
    public int getPoints() { return points; }
    public EnemySprite getEnemyView() { return enemyView; }


}