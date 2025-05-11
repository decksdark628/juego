package Controlador;

import Controlador.Entities.*;
import Controlador.Projectiles.*;
import Controlador.Utils.*;

import Modelo.GameConstants;


import javafx.scene.layout.AnchorPane;

import java.util.*;

public class GameEngine {

    private Player player;
    private PlayerSprite playerView;
    private List<Enemy> enemies;
    private List<PlayerProjectile> playerProjectiles;
    private List<EnemyProjectile> enemyProjectiles;
    private ProjectileManager projectileManager;
    private AnchorPane rootPane;
    private int elapsedSeconds;
    private SoundManager soundManager;

    public GameEngine(AnchorPane rootPane) {
        this.rootPane = rootPane;
        this.enemies = new ArrayList<>();
        this.playerProjectiles = new ArrayList<>();
        this.enemyProjectiles = new ArrayList<>();
        this.elapsedSeconds = 0;
        this.soundManager = new SoundManager();

        createPlayer();
        this.projectileManager = new ProjectileManager(playerProjectiles, enemyProjectiles, player, rootPane, soundManager);
    }

    private void createPlayer() {
        player = new Player(GameConstants.MAP_WIDTH / 2, GameConstants.MAP_HEIGHT / 2);
        playerView = new PlayerSprite(player, rootPane);
        rootPane.getChildren().add(playerView);
        playerView.toFront();
    }

    public Player getPlayer() { return player;}
    public PlayerSprite getPlayerView() { return playerView;}
    public List<Enemy> getEnemies() {return enemies;}
    public List<PlayerProjectile> getPlayerProjectiles() { return playerProjectiles; }
    public List<EnemyProjectile> getEnemyProjectiles() { return enemyProjectiles;}
    public int getElapsedSeconds() { return elapsedSeconds;}
    
    public void incrementElapsedSeconds() {
        elapsedSeconds++;
    }

    public void updatePlayerMovement(double dx, double dy, double deltaTime) {
        handlePlayerInput(dx, dy);
        movePlayer(deltaTime);
    }

    private void handlePlayerInput(double dx, double dy) {
        player.setMovementKeyPressed("W", dy < 0);
        player.setMovementKeyPressed("A", dx < 0);
        player.setMovementKeyPressed("S", dy > 0);
        player.setMovementKeyPressed("D", dx > 0);
    }

    private void movePlayer(double deltaTime) {
        player.move(deltaTime);
        playerView.update();
    }

    public void updateEnemies(double deltaTime) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (!enemy.update(player.getX(), player.getY(), player, enemies, deltaTime)) {
                if (enemy instanceof Enemy3) {
                    ((Enemy3) enemy).onDeath(player, enemies);
                    soundManager.playExplosion();
                    double centerEnemyX = enemy.getX() + enemy.getWidth() / 2.0;
                    double centerEnemyY = enemy.getY() + enemy.getHeight() / 2.0;
                    double centerPlayerX = player.getX() + player.getWidth() / 2.0;
                    double centerPlayerY = player.getY() + player.getHeight() / 2.0;
                    double distance = Math.hypot(centerPlayerX - centerEnemyX, centerPlayerY - centerEnemyY);
                    if (distance <= ((Enemy3) enemy).getExplosionRadius()) {
                        soundManager.playPlayerDamage();
                        playerView.flashRed();
                    }
                }
                soundManager.playEnemyDeath();
                removeEnemyFromScene(enemy);
                iterator.remove();
            }
        }

        handleEnemyExplosions(); 
        updateEnemyViews(); 
    }

    private void updateEnemyViews() {
        for (Enemy enemy : enemies) {
            EnemySprite enemyView = findEnemyView(enemy);
            if (enemyView != null) {
                enemyView.update();
            }
        }
    }

    private void handleEnemyExplosions() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof Enemy3) {
                Enemy3 enemy3 = (Enemy3) enemy;
                if (enemy3.isDead() && !enemy3.getExplodedVisual()) {
                    EnemySprite enemyView = findEnemyView(enemy3);
                    if (enemyView != null) {
                        enemyView.showExplosion(enemy3.getExplosionRadius());
                        soundManager.playExplosion();
                        double cEx = enemy3.getX() + enemy3.getWidth()/2.0;
                        double cEy = enemy3.getY() + enemy3.getHeight()/2.0;
                        double cPx = player.getX() + player.getWidth()/2.0;
                        double cPy = player.getY() + player.getHeight()/2.0;
                        if (Math.hypot(cPx - cEx, cPy - cEy) <= enemy3.getExplosionRadius()) {
                            soundManager.playPlayerDamage();
                            playerView.flashRed();
                        }
                        enemy3.setExplodedVisual(true);
                    }
                }
            }
        }
    }

    private EnemySprite findEnemyView(Enemy enemy) {
        for (javafx.scene.Node node : rootPane.getChildren()) {
            if (node instanceof EnemySprite && ((EnemySprite) node).getEnemyModel() == enemy) {
                return (EnemySprite) node;
            }
        }
        return null;
    }

    private void removeEnemyFromScene(Enemy enemy) {
        EnemySprite enemyView = findEnemyView(enemy);
        if (enemyView != null) {
            rootPane.getChildren().remove(enemyView);
        }
    }

    public void updateProjectiles(double deltaTime) {
        projectileManager.updateProjectilesViews();
    }

    public void checkCollisions() {
        checkProjectileCollisionsWithEnemies();
        checkPlayerCollisionsWithEnemies();
        checkEnemyProjectilesWithPlayer(); 
    }

    private void checkProjectileCollisionsWithEnemies() {
        projectileManager.checkCollisionsWithEnemies(enemies);
    }

    private void checkPlayerCollisionsWithEnemies() {
        for (Enemy enemy : enemies) {
            if (player.collidesWith(enemy) && enemy.canDamagePlayer()) {
                player.hit(enemy.getDamage()); 
                soundManager.playPlayerDamage();   
                playerView.flashRed();
            }
        }
    }

    private void checkEnemyProjectilesWithPlayer() {
        Iterator<EnemyProjectile> iterator = enemyProjectiles.iterator();
        while (iterator.hasNext()) {
            EnemyProjectile projectile = iterator.next();
            if (player.collidesWith(projectile)) {
                player.hit(projectile.getDamage()); 
                soundManager.playPlayerDamage();
                playerView.flashRed();
                removeProjectileFromScene(projectile); 
                iterator.remove(); 
            }
        }
    }

    private void removeProjectileFromScene(EnemyProjectile projectile) {
        ProjectileView view = projectile.getView();
        if (view != null) {
            rootPane.getChildren().remove(view);
        }
    }

    public void addPlayerProjectile(PlayerProjectile proj, ProjectileView view) {
        playerProjectiles.add(proj);
        rootPane.getChildren().add(view);
    }

    public void addEnemy(Enemy enemy, EnemySprite view) {
        enemy.setEnemyView(view); 
        enemies.add(enemy);
        rootPane.getChildren().add(view);
    }

    public void updateGame(double deltaTime) {
        incrementElapsedSeconds();
        updatePlayerMovement(0, 0, deltaTime); 
        updateEnemies(deltaTime); 
        updateProjectiles(deltaTime); 
        checkCollisions(); 
        regeneratePlayerHp(); 
    }

    public void regeneratePlayerHp() {
            player.regenerateHp();      
    }
}
