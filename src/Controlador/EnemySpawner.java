package Controlador;

import Controlador.Entities.*;
import Controlador.Projectiles.*;
import Modelo.GameConstants;

import java.util.List;
import java.util.Random;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class EnemySpawner {

    //private List<Enemy> enemies;
    private List<EnemyProjectile> enemyProjectiles;
    private GameEngine gameEngine;

    private Random random;
    private int currentWave = 1;
    private int enemiesPerWave = 3; 
    private int waveCooldown = 10;
    private double speedScale = 0.1;
    private int hpScale = 3;
    private int pointsScale = 5;
    private double shootIntervalScale = 100;
    private int hpScale2 = 10;
    private int pointsScale2 = 10;
    private int damageScale = 5;
    private Timeline waveTimer;
    
    private final static int SCALE_INTERVAL = 6;
    private final static int ENEMY_REDUCTION_PER_SCALE = 3;
    private final static int ENEMY_INCREMENT_PER_SCALE = 1;

    public EnemySpawner(/*List<Enemy> enemies, */List<EnemyProjectile> enemyProjectiles, GameEngine gameEngine) {
        //this.enemies = enemies;
        this.enemyProjectiles = enemyProjectiles;
        this.gameEngine = gameEngine;
        this.random = new Random();
        setupWaveTimer();
        spawnWaveEnemies();
    }

    private void setupWaveTimer() {
        waveTimer = new Timeline(new KeyFrame(Duration.seconds(waveCooldown), event -> {
            startNextWave();
        }));
        waveTimer.setCycleCount(Timeline.INDEFINITE);
        waveTimer.play();
    }

    private void startNextWave() {
        currentWave++;
        enemiesPerWave += ENEMY_INCREMENT_PER_SCALE;

        if (currentWave % SCALE_INTERVAL == 0) {
            enemiesPerWave = Math.max(1, enemiesPerWave - ENEMY_REDUCTION_PER_SCALE);
        }
        spawnWaveEnemies();
    }

    private void spawnWaveEnemies() {
        int remaining = enemiesPerWave;

        int enemy3ToSpawn = 0;
        if (currentWave >=  GameConstants.ENEMY3_FIRST_SPAWN_WAVE) {
            enemy3ToSpawn = Math.min(1 + (currentWave - GameConstants.ENEMY3_FIRST_SPAWN_WAVE) / GameConstants.ENEMY3_WAVE_INTERVAL, remaining);
            remaining -= enemy3ToSpawn;
        }

        int enemy2ToSpawn = 0;
        if (currentWave >= GameConstants.ENEMY2_FIRST_SPAWN_WAVE) {
            enemy2ToSpawn = Math.min(1 + (currentWave - GameConstants.ENEMY2_FIRST_SPAWN_WAVE) / GameConstants.ENEMY2_WAVE_INTERVAL, remaining);
            remaining -= enemy2ToSpawn;
        }

        int enemy1ToSpawn = remaining; 

        for (int i = 0; i < enemy3ToSpawn; i++) {
            spawnEnemy(createEnemyInstance(Enemy3.class));
        }
        for (int i = 0; i < enemy2ToSpawn; i++) {
            spawnEnemy(createEnemyInstance(Enemy2.class));
        }
        for (int i = 0; i < enemy1ToSpawn; i++) {
            spawnEnemy(createEnemyInstance(Enemy1.class));
        }
    }

    private Enemy createEnemyInstance(Class<? extends Enemy> enemyClass) {
        if (enemyClass == Enemy1.class) {
            return applyScaling(new Enemy1(0, 0));
        } else if (enemyClass == Enemy2.class) {
            return applyScaling(new Enemy2(0, 0, enemyProjectiles));
        } else if (enemyClass == Enemy3.class) {
            return applyScaling(new Enemy3(0, 0));
        }
        return null;
    }

    private Enemy applyScaling(Enemy enemy) {
        int firstScaleWave;
        int scaleCount = 0;

        if (enemy instanceof Enemy1) {
            firstScaleWave = GameConstants.ENEMY1_FIRST_SPAWN_WAVE + SCALE_INTERVAL;  
        } else if (enemy instanceof Enemy2) {
            firstScaleWave = GameConstants.ENEMY2_FIRST_SPAWN_WAVE + SCALE_INTERVAL; 
        } else {
            firstScaleWave = GameConstants.ENEMY3_FIRST_SPAWN_WAVE + SCALE_INTERVAL; 
        }

        if (currentWave >= firstScaleWave) {
            scaleCount = ((currentWave - firstScaleWave) / SCALE_INTERVAL) + 1;
        }

        if (scaleCount > 0) {
            if (enemy instanceof Enemy1) {
                enemy.setSpeed(enemy.getSpeed() + speedScale * scaleCount);
                enemy.setPoints(enemy.getPoints() + pointsScale * scaleCount);
                enemy.setHp(enemy.getHp() + hpScale * scaleCount);
            } else if (enemy instanceof Enemy2) {
                enemy.setHp(enemy.getHp() + hpScale * scaleCount);
                enemy.setPoints(enemy.getPoints() + pointsScale * scaleCount);
                double newInterval = Math.max(2000, enemy.getShootInterval() - shootIntervalScale * scaleCount);
                enemy.setShootInterval(newInterval);
            } else if (enemy instanceof Enemy3) {
                enemy.setHp(enemy.getHp() + hpScale2 * scaleCount);
                enemy.setDamage(enemy.getDamage() + damageScale * scaleCount);
                enemy.setPoints(enemy.getPoints() + pointsScale2 * scaleCount);
            }
        }
        return enemy;
    }

    private void spawnEnemy(Enemy baseEnemy) {
        double[] spawnPosition = calculateSpawnPosition();
        Enemy enemy = createEnemyWithPosition(baseEnemy, spawnPosition[0], spawnPosition[1]);
        addEnemyToGame(enemy);
    }

    private double[] calculateSpawnPosition() {
        Player player = gameEngine.getPlayer();
        double playerCenterX = player.getX() + player.getWidth() / 2.0;
        double playerCenterY = player.getY() + player.getHeight() / 2.0;

        double x = 0, y = 0;
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;
        final double MIN_DISTANCE = 600;

        do {
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
            attempts++;
        } while (attempts < MAX_ATTEMPTS &&
                 Math.hypot(x - playerCenterX, y - playerCenterY) < MIN_DISTANCE);

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
