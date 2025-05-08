package Controlador.Entities;

import Controlador.Entities.Player;
import Controlador.Entities.Enemy2;
import Controlador.Entities.Enemy;
import Controlador.Entities.Enemy1;
import Modelo.ImagePaths;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Point2D;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class EnemySprite extends ImageView {
    private Enemy enemyModel;
    private Player playerModel; 

    protected EnemySprite enemyView;

    public EnemySprite(Enemy enemy, Player player) {
        this.enemyModel = enemy;
        this.playerModel = player;
        
        String imagePath = determineEnemyImage(enemy);
        setImage(new Image(getClass().getResourceAsStream(imagePath)));
        
        setFitWidth(enemyModel.getWidth());
        setFitHeight(enemyModel.getHeight());
        setLayoutX(enemyModel.getX());
        setLayoutY(enemyModel.getY());
        
        setTranslateX(-getFitWidth() / 2);
        setTranslateY(-getFitHeight() / 2);
    }

    private String determineEnemyImage(Enemy enemy) {
        if (enemy instanceof Enemy1) {
            return ImagePaths.ZOMBIE;
        } else if (enemy instanceof Enemy2) {
            return ImagePaths.ENEMY_2;
        } else {
            return ImagePaths.ENEMY_3;
        }
    }

    public void update() {
        updatePosition();
        updateRotation();
    }

    private void updatePosition() {
        setLayoutX(enemyModel.getX());
        setLayoutY(enemyModel.getY());
    }

    private void updateRotation() {
        setRotate(getEnemyRotation());
    }

    private double getEnemyRotation() {
        if (isEnemyMovingAway()) {
            return calculateRotationForMovingAway();
        }
        return calculateRotationTowardsPlayer();
    }

    private boolean isEnemyMovingAway() {
        return enemyModel instanceof Enemy2 && ((Enemy2) enemyModel).isMovingAway();
    }

    private double calculateRotationForMovingAway() {
        double moveDx = ((Enemy2) enemyModel).getMoveDx();
        double moveDy = ((Enemy2) enemyModel).getMoveDy();
        return Math.toDegrees(Math.atan2(moveDy, moveDx));
    }

    private double calculateRotationTowardsPlayer() {
        double playerX = playerModel.getX() + playerModel.getWidth() / 2;
        double playerY = playerModel.getY() + playerModel.getHeight() / 2;
        double enemyX = enemyModel.getX() + enemyModel.getWidth() / 2;
        double enemyY = enemyModel.getY() + enemyModel.getHeight() / 2;

        double dx = playerX - enemyX;
        double dy = playerY - enemyY;
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    public Enemy getEnemyModel() {
        return enemyModel;
    }
    
    public Point2D getProjectileSpawnPoint() {
        double centerX = getLayoutX() + getTranslateX() + getFitWidth() / 2.0;
        double centerY = getLayoutY() + getTranslateY() + getFitHeight() / 2.0;
        double angle = Math.toRadians(getRotate());

        double localOffsetX = getFitWidth() / 2.0; 
        double localOffsetY = 0;                   

        double globalOffsetX = localOffsetX * Math.cos(angle) - localOffsetY * Math.sin(angle);
        double globalOffsetY = localOffsetX * Math.sin(angle) + localOffsetY * Math.cos(angle);

        double spawnX = centerX + globalOffsetX;
        double spawnY = centerY + globalOffsetY;

        return new Point2D(spawnX, spawnY);
    }    
    
    public void showExplosion(double radius) {
        Circle explosion = new Circle(radius);
        explosion.setCenterX(getLayoutX() + getTranslateX() + getFitWidth() / 2.0);
        explosion.setCenterY(getLayoutY() + getTranslateY() + getFitHeight() / 2.0);
        explosion.setFill(Color.rgb(255, 120, 0, 0.5));
        explosion.setStroke(Color.ORANGE);
        explosion.setStrokeWidth(4);

        if (getParent() instanceof javafx.scene.layout.Pane) {
            javafx.scene.layout.Pane parent = (javafx.scene.layout.Pane) getParent();
            parent.getChildren().add(explosion);

            ScaleTransition scale = new ScaleTransition(Duration.millis(350), explosion);
            scale.setFromX(0.3);
            scale.setFromY(0.3);
            scale.setToX(1.0);
            scale.setToY(1.0);

            FadeTransition fade = new FadeTransition(Duration.millis(350), explosion);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            fade.setOnFinished(e -> parent.getChildren().remove(explosion));

            scale.play();
            fade.play();
        }
    }    
    
    public void setEnemyView(EnemySprite enemyView) {
        this.enemyView = enemyView;
    }

    public EnemySprite getEnemyView() {
        return enemyView;
    }
}