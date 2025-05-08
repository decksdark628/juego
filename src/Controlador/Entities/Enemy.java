package Controlador.Entities;

import Controlador.Projectiles.EnemyProjectile;
import java.util.List;
import Modelo.GameConstants;

public abstract class Enemy extends GameEntity {
    protected int hp;
    protected int damage;
    protected int points;
    protected long lastDamageTime;
    protected int firstSpawn;
    protected int spawnCooldown;
    protected int maxEnemies;
    protected long lastShotTime;
    protected double minShootDistance;
    protected double maxShootDistance;
    protected double shootInterval;
    protected List<EnemyProjectile> enemyProjectiles;
    protected EnemySprite enemyView;

    public Enemy(double x, double y, double width, double height, double speed,
                int hp, int damage, int points, int firstSpawn, int spawnCooldown,
                int maxEnemies) {
        super(x, y, width, height, speed);
        this.hp = hp;
        this.damage = damage;
        this.points = points;
        this.firstSpawn = firstSpawn;
        this.spawnCooldown = spawnCooldown;
        this.maxEnemies = maxEnemies;
    }
    
    protected void moveTowardsPlayer(double playerX, double playerY, List<Enemy> allEnemies) {
        double[] direction = calculateDirection(playerX, playerY);
        moveInDirection(direction[0], direction[1]);
    }

    private double[] calculateDirection(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > 0) {
            dx = (dx / distance) * speed;
            dy = (dy / distance) * speed;
        }
        return new double[]{dx, dy};
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

    public boolean update(double playerX, double playerY, Player player, List<Enemy> allEnemies) {
        if (isDead()) {
            return false; 
        }

        updateMovement(playerX, playerY, allEnemies);
        updateAttacks(playerX, playerY, player, allEnemies);
        return true; 
    }

    public abstract void updateMovement(double playerX, double playerY, List<Enemy> allEnemies);
    public abstract void updateAttacks(double playerX, double playerY, Player player, List<Enemy> allEnemies);

    public void setHp(int hp) { this.hp=hp; }
    public void setEnemyView(EnemySprite enemyView) { this.enemyView = enemyView; }
    public int getHp() { return hp; }
    public int getDamage() { return damage; }
    public int getPoints() { return points; }
    public int getFirstSpawn() { return firstSpawn; }
    public int getSpawnCooldown() { return spawnCooldown; }
    public int getMaxEnemies() { return maxEnemies; }
    public EnemySprite getEnemyView() { return enemyView; }
}