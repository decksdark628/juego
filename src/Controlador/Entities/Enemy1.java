package Controlador.Entities;


import java.util.List;

public class Enemy1 extends Enemy {
    public Enemy1(double x, double y) {
        super(x, y, 65, 65, 0.4, 2, 20, 10, 1, 1, 35);
    }

    @Override
    public void updateMovement(double playerX, double playerY, List<Enemy> allEnemies) {
        moveTowardsPlayer(playerX, playerY, allEnemies);
    }

    @Override
    public void updateAttacks(double playerX, double playerY, Player player, List<Enemy> allEnemies) {
        
    }
}