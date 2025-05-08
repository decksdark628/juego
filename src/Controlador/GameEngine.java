package Controlador;

import Controlador.Utils.ProjectileManager;
import Controlador.Entities.Player;
import Controlador.Entities.Enemy3;
import Controlador.Entities.Enemy;
import Controlador.Projectiles.PlayerProjectile;
import Controlador.Projectiles.EnemyProjectile;
import Modelo.GameConstants;
import Controlador.Entities.EnemySprite;
import Controlador.Entities.PlayeSprite;
import Controlador.Projectiles.ProjectileView;
import Controlador.Utils.ProjectileManager;
import Controlador.Utils.ProjectileManager;
import Controlador.Utils.ProjectileManager;

import javafx.scene.layout.AnchorPane;

import java.util.*;

public class GameEngine {

    private Player player;
    private PlayeSprite playerView;
    private List<Enemy> enemies;
    private List<PlayerProjectile> playerProjectiles;
    private List<EnemyProjectile> enemyProjectiles;
    private ProjectileManager projectileManager;
    private AnchorPane rootPane;
    private int elapsedSeconds;

    public GameEngine(AnchorPane rootPane) {
        this.rootPane = rootPane;
        this.enemies = new ArrayList<>();
        this.playerProjectiles = new ArrayList<>();
        this.enemyProjectiles = new ArrayList<>();
        this.elapsedSeconds = 0;

        createPlayer();
        this.projectileManager = new ProjectileManager(playerProjectiles, enemyProjectiles, player, rootPane);
    }

    private void createPlayer() {
        player = new Player(GameConstants.MAP_WIDTH / 2, GameConstants.MAP_HEIGHT / 2);
        playerView = new PlayeSprite(player, rootPane);
        rootPane.getChildren().add(playerView);
        playerView.toFront();
    }

    public Player getPlayer() { return player;}
    public PlayeSprite getPlayerView() { return playerView;}
    public List<Enemy> getEnemies() {return enemies;}
    public List<PlayerProjectile> getPlayerProjectiles() { return playerProjectiles; }
    public List<EnemyProjectile> getEnemyProjectiles() { return enemyProjectiles;}
    public int getElapsedSeconds() { return elapsedSeconds;}
    
    public void incrementElapsedSeconds() {
        elapsedSeconds++;
    }

    public void updatePlayerMovement(double dx, double dy) {
        handlePlayerInput(dx, dy);
        movePlayer();
    }

    private void handlePlayerInput(double dx, double dy) {
        player.setMovementKeyPressed("W", dy < 0);
        player.setMovementKeyPressed("A", dx < 0);
        player.setMovementKeyPressed("S", dy > 0);
        player.setMovementKeyPressed("D", dx > 0);
    }

    private void movePlayer() {
        player.move();
        playerView.update();
    }

    public void updateEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (!enemy.update(player.getX(), player.getY(), player, enemies)) {
                if (enemy instanceof Enemy3) {
                    ((Enemy3) enemy).onDeath(player, enemies);
                }
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

    public void updateProjectiles() {
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
            }
        }
    }

    private void checkEnemyProjectilesWithPlayer() {
        Iterator<EnemyProjectile> iterator = enemyProjectiles.iterator();
        while (iterator.hasNext()) {
            EnemyProjectile projectile = iterator.next();
            if (player.collidesWith(projectile)) {
                player.hit(projectile.getDamage()); 
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

    public void updateGame() {
        incrementElapsedSeconds();
        updatePlayerMovement(0, 0); 
        updateEnemies(); 
        updateProjectiles(); 
        checkCollisions(); 
        regeneratePlayerHp(); 
    }

    public void regeneratePlayerHp() {
            player.regenerateHp();      
    }
}
