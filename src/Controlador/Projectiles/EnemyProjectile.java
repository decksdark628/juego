package Controlador.Projectiles;

import Controlador.Entities.Enemy;
import Modelo.GameConstants;

public class EnemyProjectile extends Projectile {
    private ProjectileView view;

    public EnemyProjectile(double startX, double startY, double targetX, double targetY, Enemy enemy) {
        super(startX, startY, 30, 15, GameConstants.ENEMY_PROJECTILE_SPEED, enemy.getDamage());
        calculateDirection(targetX, targetY);
    }

    public void setView(ProjectileView view) {
        this.view = view;
    }

    public ProjectileView getView() {
        return view;
    }
}