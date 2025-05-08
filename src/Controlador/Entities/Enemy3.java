package Controlador.Entities;

import java.util.List;

public class Enemy3 extends Enemy {
    
    private boolean exploded = false;
    private final double explosionTriggerDistance = 100.0;
    private final double explosionRadius = 200.0;
    private boolean explodedVisual = false;
    
    public Enemy3(double x, double y) {
        super(x, y, 60, 60, 1.0, 
              3, 40, 80, 
              1, 10, 2);
    }

    @Override
    public void updateMovement(double playerX, double playerY, List<Enemy> allEnemies) {
        moveTowardsPlayer(playerX, playerY, allEnemies);
    }

    @Override
    public void updateAttacks(double playerX, double playerY, Player player, List<Enemy> allEnemies) {
        if (exploded) return;

        if (isWithinExplosionTriggerDistance(playerX, playerY)) {
            triggerExplosion();
            explode(player, allEnemies);
            if (enemyView != null) {
                enemyView.showExplosion(explosionRadius); 
            }
        }
    }

    private boolean isWithinExplosionTriggerDistance(double playerX, double playerY) {
        double dx = playerX - (x + width / 2);
        double dy = playerY - (y + height / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= explosionTriggerDistance;
    }

    private void triggerExplosion() {
        exploded = true;
        this.setHp(0); 
    }
    
    public void explode(Player player, List<Enemy> enemies) {
        explodeWithRadius(explosionRadius, player, enemies); 
    }

    private double getCenterX() {
        return x + width / 2.0;
    }

    private double getCenterY() {
        return y + height / 2.0;
    }

    private void damagePlayerIfInRange(Player player, double centerX, double centerY, int damage, double radius) {
        double dx = player.getX() + player.getWidth() / 2.0 - centerX;
        double dy = player.getY() + player.getHeight() / 2.0 - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= radius) {
            player.hit(damage);
        }
    }


    private void damageEnemiesIfInRange(List<Enemy> enemies, double centerX, double centerY, int damage, double radius) {
        for (Enemy e : enemies) {
            if (e == this) continue;

            if (isEnemyWithinExplosionRadius(e, centerX, centerY, radius)) {
                e.hit(damage);
            }
        }
    }

    private boolean isEnemyWithinExplosionRadius(Enemy enemy, double centerX, double centerY, double radius) {
        double ex = enemy.getX() + enemy.getWidth() / 2.0 - centerX;
        double ey = enemy.getY() + enemy.getHeight() / 2.0 - centerY;
        double edist = Math.sqrt(ex * ex + ey * ey);
        return edist <= radius;
    }

    public boolean getExploded() { return this.exploded;}
    public double getExplosionRadius() { return this.explosionRadius;}
    public boolean getExplodedVisual() { return this.explodedVisual; }
    public void setExplodedVisual(boolean v) { this.explodedVisual = v; }
    
   
    @Override
    public boolean isDead() {
        return exploded || super.isDead();
    }
    
    public void onDeath(Player player, List<Enemy> enemies) {
        if (!exploded &&isDead()) { 
            exploded = true;
            double reducedExplosionRadius = explosionRadius / 2.0; 
            explodeWithRadius(reducedExplosionRadius, player, enemies);
        }
    }

    private void explodeWithRadius(double radius, Player player, List<Enemy> enemies) {
        double centerX = getCenterX();
        double centerY = getCenterY();
        int damage = getDamage();

        damagePlayerIfInRange(player, centerX, centerY, damage, radius);

        damageEnemiesIfInRange(enemies, centerX, centerY, damage, radius);

        if (enemyView != null) {
            enemyView.showExplosion(radius);
        }
    }

}