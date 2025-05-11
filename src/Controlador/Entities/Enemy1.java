package Controlador.Entities;


import java.util.List;

import Modelo.GameConstants;

public class Enemy1 extends Enemy {
    public Enemy1(double x, double y) {
        super(x, y, 65, 65, (1.0*GameConstants.N), 20, 20, 15);
    }

    @Override
    public void updateMovement(double playerX, double playerY, List<Enemy> allEnemies, double deltaTime) {
        moveTowardsPlayer(playerX, playerY, allEnemies, deltaTime);
    }

    @Override
    public void updateAttacks(double playerX, double playerY, Player player, List<Enemy> allEnemies, double deltaTime) {
        
    }
}