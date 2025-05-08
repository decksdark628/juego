package Controlador.Projectiles;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Controlador.Projectiles.Projectile;
import Modelo.ImagePaths;

public class ProjectileView extends ImageView {
    private Projectile projectileModel;
    private boolean isPlayerProjectile;

    public ProjectileView(Projectile projectile, boolean isPlayerProjectile) {
        this.projectileModel = projectile;
        this.isPlayerProjectile = isPlayerProjectile;

        setImageBasedOnType();
        configureSize();
        configureInitialPosition();
        updateRotation();
    }

    private void setImageBasedOnType() {
        String imagePath = isPlayerProjectile 
            ? ImagePaths.BALA 
            : ImagePaths.BALA_2;
        setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }

    private void configureSize() {
        setFitWidth(projectileModel.getWidth());
        setFitHeight(projectileModel.getHeight());
    }

    private void configureInitialPosition() {
        setLayoutX(projectileModel.getX());
        setLayoutY(projectileModel.getY());
        setTranslateX(-getFitWidth() / 2);
        setTranslateY(-getFitHeight() / 2);
    }

    public void update() {
        updatePosition();
        updateRotation();
    }

    private void updatePosition() {
        setLayoutX(projectileModel.getX());
        setLayoutY(projectileModel.getY());
    }

    private void updateRotation() {
        double angle = calculateRotationAngle();
        setRotate(angle);
    }

    private double calculateRotationAngle() {
        return Math.toDegrees(Math.atan2(projectileModel.getDy(), projectileModel.getDx()));
    }

    public Projectile getProjectileModel() {
        return projectileModel;
    }

    public boolean isPlayerProjectile() {
        return isPlayerProjectile;
    }
}