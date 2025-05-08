package Controlador;

import Controlador.Entities.Enemy2;
import Controlador.Entities.Enemy3;
import Controlador.Entities.Enemy;
import Controlador.Entities.Enemy1;
import Controlador.Projectiles.EnemyProjectile;
import Controlador.Entities.EnemySprite;
import Modelo.GameConstants;

import java.util.List;
import java.util.Random;

public class EnemySpawner {

    private List<Enemy> enemies;
    private List<EnemyProjectile> enemyProjectiles;
    private GameEngine gameEngine;

    private long lastEnemy1Spawn = 0;
    private long lastEnemy2Spawn = 0;
    private long lastEnemy3Spawn = 0;

    private Random random;

    public EnemySpawner(List<Enemy> enemies, List<EnemyProjectile> enemyProjectiles, GameEngine gameEngine) {
        this.enemies = enemies;
        this.enemyProjectiles = enemyProjectiles;
        this.gameEngine = gameEngine;
        this.random = new Random();
    }
    public void spawnEnemies(int elapsedSeconds) {
        long currentTime = System.currentTimeMillis();

        spawnEnemyType(Enemy1.class, elapsedSeconds, currentTime);
        spawnEnemyType(Enemy2.class, elapsedSeconds, currentTime);
        spawnEnemyType(Enemy3.class, elapsedSeconds, currentTime);
    }

    private void spawnEnemyType(Class<? extends Enemy> enemyClass, int elapsedSeconds, long currentTime) {
        Enemy sampleEnemy = createSampleEnemy(enemyClass);
        if (sampleEnemy == null) return;

        int currentCount = countEnemiesOfType(enemyClass);
        if (currentCount >= sampleEnemy.getMaxEnemies()) return;

        if (elapsedSeconds >= sampleEnemy.getFirstSpawn() &&
            currentTime - getLastSpawnTime(enemyClass) >= sampleEnemy.getSpawnCooldown() * 1000) {
            spawnEnemy(createEnemyInstance(enemyClass));
            updateLastSpawnTime(enemyClass, currentTime);
        }
    }

    private Enemy createSampleEnemy(Class<? extends Enemy> enemyClass) {
        if (enemyClass == Enemy1.class) {
            return new Enemy1(0, 0);
        } else if (enemyClass == Enemy2.class) {
            return new Enemy2(0, 0, enemyProjectiles);
        } else if (enemyClass == Enemy3.class) {
            return new Enemy3(0, 0);
        }
        return null;
    }

    private Enemy createEnemyInstance(Class<? extends Enemy> enemyClass) {
        if (enemyClass == Enemy1.class) {
            return new Enemy1(0, 0);
        } else if (enemyClass == Enemy2.class) {
            return new Enemy2(0, 0, enemyProjectiles);
        } else if (enemyClass == Enemy3.class) {
            return new Enemy3(0, 0);
        }
        return null;
    }

    private int countEnemiesOfType(Class<? extends Enemy> enemyClass) {
        return (int) enemies.stream().filter(enemyClass::isInstance).count();
    }

    private long getLastSpawnTime(Class<? extends Enemy> enemyClass) {
        if (enemyClass == Enemy1.class) return lastEnemy1Spawn;
        if (enemyClass == Enemy2.class) return lastEnemy2Spawn;
        if (enemyClass == Enemy3.class) return lastEnemy3Spawn;
        return 0;
    }

    private void updateLastSpawnTime(Class<? extends Enemy> enemyClass, long currentTime) {
        if (enemyClass == Enemy1.class) lastEnemy1Spawn = currentTime;
        if (enemyClass == Enemy2.class) lastEnemy2Spawn = currentTime;
        if (enemyClass == Enemy3.class) lastEnemy3Spawn = currentTime;
    }

    private void spawnEnemy(Enemy baseEnemy) {
        double[] spawnPosition = calculateSpawnPosition();
        Enemy enemy = createEnemyWithPosition(baseEnemy, spawnPosition[0], spawnPosition[1]);
        addEnemyToGame(enemy);
    }

    private double[] calculateSpawnPosition() {
        double x = 0, y = 0;
        int edge = random.nextInt(4);
        switch (edge) {
            case 0: 
                x = random.nextDouble() * GameConstants.MAP_WIDTH; 
                y = 0; 
                break;
            case 1: 
                x = GameConstants.MAP_WIDTH; 
                y = random.nextDouble() * GameConstants.MAP_HEIGHT; 
                break;
            case 2: 
                x = random.nextDouble() * GameConstants.MAP_WIDTH; 
                y = GameConstants.MAP_HEIGHT; 
                break;
            case 3: 
                x = 0; 
                y = random.nextDouble() * GameConstants.MAP_HEIGHT; 
                break;
        }
        return new double[]{x, y};
    }

    private Enemy createEnemyWithPosition(Enemy baseEnemy, double x, double y) {
        if (baseEnemy instanceof Enemy1) {
            return new Enemy1(x, y);
        } else if (baseEnemy instanceof Enemy2) {
            return new Enemy2(x, y, enemyProjectiles);
        } else {
            return new Enemy3(x, y);
        }
    }

    private void addEnemyToGame(Enemy enemy) {
        EnemySprite view = new EnemySprite(enemy, gameEngine.getPlayer());
        gameEngine.addEnemy(enemy, view);
    }
}
