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
        super(x, y, 65, 65, (1.5*GameConstants.N), 15, 15, 25);
        this.enemyProjectiles = enemyProjectiles;
        this.minShootDistance = 300;
        this.maxShootDistance = 550;
        this.shootInterval = 2800;
        this.isMovingAway = false;
    }

    @Override
    public void updateMovement(double playerX, double playerY, List<Enemy> allEnemies, double deltaTime) {
        double distance = calculateDistance(playerX, playerY);
        isMovingAway = false;

        if (shouldMoveAway(distance)) {
            if (!isNearBoundaries()) {
                moveAway(playerX, playerY, allEnemies, deltaTime);
            } else {
                enterFrenzyMode(playerX, playerY, allEnemies, deltaTime);
            }
        } else if (shouldStopAndShoot(distance)) {
            moveTowardsPlayer(playerX, playerY, allEnemies, deltaTime); 
        } else {
            moveTowardsPlayer(playerX, playerY, allEnemies, deltaTime);
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
    public void updateAttacks(double playerX, double playerY, Player player, List<Enemy> allEnemies, double deltaTime) {
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
    
    private void moveAway(double playerX, double playerY, List<Enemy> allEnemies, double deltaTime) {
        double distance = calculateDistance(playerX, playerY);
        if (distance > 0) {
            double dx = (x - playerX) / distance;
            double dy = (y - playerY) / distance;
            
            dx = dx * speed;
            dy = dy * speed;
            
            dx *= deltaTime * 60.0;
            dy *= deltaTime * 60.0;     

            moveDx = dx;
            moveDy = dy;
                  
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

    private void enterFrenzyMode(double playerX, double playerY, List<Enemy> allEnemies, double deltaTime) {
        activateFrenzyMode();
        moveTowardsPlayer(playerX, playerY, allEnemies, deltaTime);
    }

    private void activateFrenzyMode() {
        speed+= 1.5;
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