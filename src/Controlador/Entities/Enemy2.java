package Controlador.Entities;

import Controlador.Projectiles.EnemyProjectile;
import Modelo.GameConstants;

import java.util.List;
import javafx.geometry.Point2D;

public class Enemy2 extends Enemy {
    
    private boolean isMovingAway; 
    private double moveDx, moveDy;
    private double initialSpeed=getSpeed();
    
    public Enemy2(double x, double y, List<EnemyProjectile> enemyProjectiles) {
        super(x, y, 65, 65, 1.0, 2, 15, 15, 1, 3, 5);
        this.enemyProjectiles = enemyProjectiles;
        this.minShootDistance = 300;
        this.maxShootDistance = 550;
        this.shootInterval = 2200;
        this.isMovingAway = false;
    }

    @Override
    public void updateMovement(double playerX, double playerY, List<Enemy> allEnemies) {
        double distance = calculateDistance(playerX, playerY);
        isMovingAway = false;

        if (shouldMoveAway(distance)) {
            if (!isNearBoundaries()) {
                moveAway(playerX, playerY, allEnemies);
            } else {
                frenzy(playerX, playerY, allEnemies);
            }
        } else if (shouldStopAndShoot(distance)) {
            moveTowardsPlayer(playerX, playerY, allEnemies); 
        } else {
            moveTowardsPlayer(playerX, playerY, allEnemies);
        }
    }

    private boolean shouldMoveAway(double distance) {
        resetSpeed();
        return distance <= minShootDistance;
    }

    private boolean shouldStopAndShoot(double distance) {
        if (distance + 100 < maxShootDistance) {
            this.speed = 0;
            return true;
        }
        resetSpeed();
        return false;
    }

    @Override
    public void updateAttacks(double playerX, double playerY, Player player, List<Enemy> allEnemies) {
        if (canShootAtPlayer(playerX, playerY)) {
            prepareToShoot();
            if (enemyView != null) {
                shoot(playerX, playerY, enemyView);
            }
        }
    }

    private boolean canShootAtPlayer(double playerX, double playerY) {
        return canShoot() && isInShootingRange(playerX, playerY);
    }

    private void prepareToShoot() {
        isMovingAway = false; 
    }

    private void resetSpeed() {
        speed = initialSpeed;
    }
    
    private void moveAway(double playerX, double playerY, List<Enemy> allEnemies) {
        double distance = calculateDistance(playerX, playerY);
        if (distance > 0) {
            moveDx = (x - playerX) / distance * speed; 
            moveDy = (y - playerY) / distance * speed;
            updatePosition(moveDx, moveDy);
            isMovingAway = true;
        }
    }

    public void shoot(double playerX, double playerY, EnemySprite enemyView) {
        if (enemyProjectiles != null) {
            EnemyProjectile projectile = createProjectile(playerX, playerY, enemyView);
            storeProjectile(projectile);
            updateLastShotTime();
        }
    }

    private EnemyProjectile createProjectile(double playerX, double playerY, EnemySprite enemyView) {
        Point2D spawn = enemyView.getProjectileSpawnPoint();
        return new EnemyProjectile(spawn.getX(), spawn.getY(), playerX, playerY, this);
    }

    private void storeProjectile(EnemyProjectile projectile) {
        enemyProjectiles.add(projectile);
    }

    private void updateLastShotTime() {
        lastShotTime = System.currentTimeMillis();
    }
    
    private void frenzy(double playerX, double playerY, List<Enemy> allEnemies) { 
        enterFrenzyMode(playerX, playerY, allEnemies);
    }    

    private void enterFrenzyMode(double playerX, double playerY, List<Enemy> allEnemies) {
        activateFrenzyMode();
        moveTowardsPlayer(playerX, playerY, allEnemies);
    }

    private void activateFrenzyMode() {
        speed = 3;
        minShootDistance = 0;
        maxShootDistance = 0;
    }

    private boolean isNearBoundaries() {
        return x < 0 || x + width > GameConstants.MAP_WIDTH ||
               y < 0 || y + height > GameConstants.MAP_HEIGHT;
    }
    public boolean isMovingAway() {
        return isMovingAway;
    }

    public double getMoveDx() {return moveDx;}
    public double getMoveDy() {return moveDy;}
    public double getSpeed() {return speed;}
    
}