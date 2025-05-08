package Controlador.Utils;

import Controlador.Entities.Player;
import Controlador.Entities.Enemy3;
import Controlador.Entities.Enemy;
import Controlador.Projectiles.PlayerProjectile;
import Controlador.Projectiles.Projectile;
import Controlador.Projectiles.EnemyProjectile;
import Controlador.Entities.EnemySprite;
import Controlador.Projectiles.ProjectileView;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.List;
import java.util.Iterator;

public class ProjectileManager {
    private final List<PlayerProjectile> playerProjectiles;
    private final List<EnemyProjectile> enemyProjectiles;
    private final Player player;
    private final AnchorPane gamePane;

    public ProjectileManager(List<PlayerProjectile> playerProjectiles,
                             List<EnemyProjectile> enemyProjectiles,
                             Player player,
                             AnchorPane gamePane) {
        this.playerProjectiles = playerProjectiles;
        this.enemyProjectiles = enemyProjectiles;
        this.player = player;
        this.gamePane = gamePane;
    }

    public void updateProjectiles() {
        updatePlayerProjectiles();
        updateEnemyProjectiles();
    }

    private void updatePlayerProjectiles() {
        Iterator<PlayerProjectile> iterator = playerProjectiles.iterator();
        while (iterator.hasNext()) {
            PlayerProjectile proj = iterator.next();
            proj.move();

            if (proj.isOutOfBounds()) {
                removeProjectile(proj, iterator);
            }
        }
    }

    private void updateEnemyProjectiles() {
        Iterator<EnemyProjectile> iterator = enemyProjectiles.iterator();
        while (iterator.hasNext()) {
            EnemyProjectile proj = iterator.next();
            proj.move();

            if (proj.isOutOfBounds()) {
                removeProjectile(proj, iterator);
            }
        }
    }

    public void checkCollisionsWithEnemies(List<Enemy> enemies) {
        Iterator<PlayerProjectile> projIterator = playerProjectiles.iterator();
        while (projIterator.hasNext()) {
            PlayerProjectile proj = projIterator.next();

            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();

                if (enemy.collidesWith(proj)) {
                    handleEnemyHit(enemy, proj, projIterator, enemyIterator, enemies);
                    break;
                }
            }
        }
    }

    public void checkPlayerCollisions(List<Enemy> enemies) {
        checkCollisionsWithEnemyProjectiles();
        checkCollisionsWithEnemies(enemies);
    }

    private void checkCollisionsWithEnemyProjectiles() {
        Iterator<EnemyProjectile> iterator = enemyProjectiles.iterator();
        while (iterator.hasNext()) {
            EnemyProjectile proj = iterator.next();
            if (player.collidesWith(proj)) {
                player.hit((int) proj.getDamage());
                proj.deactivate();
                removeProjectile(proj, iterator);
            }
        }
    }

    private void handleEnemyHit(Enemy enemy, PlayerProjectile proj, Iterator<PlayerProjectile> projIterator, Iterator<Enemy> enemyIterator, List<Enemy> enemies) {
        enemy.hit((int) proj.getDamage());
        proj.deactivate();

        removeProjectile(proj, projIterator);

        if (enemy.isDead()) {
            if (enemy instanceof Enemy3) {
                ((Enemy3) enemy).onDeath(player, enemies);
            }
            player.addScore(enemy.getPoints());
            player.addAuxScore(enemy.getPoints());
            removeEnemyFromScene(enemy);
            enemyIterator.remove();
        }
    }

    public void updateProjectilesViews() {
        updatePlayerProjectilesViews();
        updateEnemyProjectilesViews();
    }

    private void updatePlayerProjectilesViews() {
        Iterator<PlayerProjectile> pIt = playerProjectiles.iterator();
        while (pIt.hasNext()) {
            PlayerProjectile p = pIt.next();
            p.move();

            if (p.getView() != null) {
                p.getView().update();
                if (p.isOutOfBounds() || !p.isActive()) {
                    removeProjectile(p, pIt);
                }
            } else {
                pIt.remove();
            }
        }
    }

    private void updateEnemyProjectilesViews() {
        Iterator<EnemyProjectile> eIt = enemyProjectiles.iterator();
        while (eIt.hasNext()) {
            EnemyProjectile p = eIt.next();
            p.move();

            if (p.getView() == null) {
                ProjectileView view = new ProjectileView(p, false);
                p.setView(view);
                gamePane.getChildren().add(view);
            }

            p.getView().update();

            if (!p.isActive() || p.isOutOfBounds()) {
                removeProjectile(p, eIt);
            }
        }
    }

    private void removeProjectile(Projectile proj, Iterator<?> iterator) {
        if (proj.getView() != null) {
            gamePane.getChildren().remove(proj.getView());
        }
        iterator.remove();
    }

    private void removeEnemyFromScene(Enemy enemy) {
        Node toRemove = null;
        for (Node node : gamePane.getChildren()) {
            if (node instanceof EnemySprite) {
                EnemySprite view = (EnemySprite) node;
                if (view.getEnemyModel() == enemy) {
                    toRemove = view;
                    break;
                }
            }
        }
        if (toRemove != null) {
            gamePane.getChildren().remove(toRemove);
        }
    }

    public void addPlayerProjectile(PlayerProjectile projectile) {
        playerProjectiles.add(projectile);
    }

    public void addEnemyProjectile(EnemyProjectile projectile) {
        enemyProjectiles.add(projectile);
    }
}
