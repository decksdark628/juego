package Controlador.Projectiles;

import Modelo.GameConstants;

public class PlayerProjectile extends Projectile {
    private ProjectileView view;

    public PlayerProjectile(double startX, double startY, double targetX, double targetY, double damage) {
        super(startX, startY, 20, 12, GameConstants.PLAYER_PROJECTILE_SPEED, damage);
        calculateDirection(targetX, targetY);
    }

    public void setView(ProjectileView view) {
        this.view = view;
    }

    public ProjectileView getView() {
        return view;
    }
}