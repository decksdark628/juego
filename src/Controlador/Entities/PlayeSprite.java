package Controlador.Entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Controlador.Entities.Player;
import Modelo.ImagePaths;
import javafx.scene.layout.AnchorPane;

public class PlayeSprite extends ImageView {
    private Player playerModel;
    private AnchorPane gamePane;

    public PlayeSprite(Player player, AnchorPane gamePane) {
        this.playerModel = player;
        this.gamePane = gamePane;

        setImage(new Image(getClass().getResourceAsStream(ImagePaths.PLAYER)));
        setFitWidth(playerModel.getWidth());
        setFitHeight(playerModel.getHeight());
        setLayoutX(playerModel.getX());
        setLayoutY(playerModel.getY());
        setTranslateX(-getFitWidth() / 2);
        setTranslateY(-getFitHeight() / 2);
    }

    public void update() {
        updatePosition();
        updateRotation();
    }

    private void updatePosition() {
        setLayoutX(playerModel.getX());
        setLayoutY(playerModel.getY());
    }

    private void updateRotation() {
        setRotate(Math.toDegrees(playerModel.getAngle()));
    }

    public void rotatePlayer(double mouseSceneX, double mouseSceneY) {
        javafx.geometry.Point2D worldMousePosition = calculateWorldMousePosition(mouseSceneX, mouseSceneY);
        double angleRad = calculateRotationAngle(worldMousePosition);
        updateRotationAngle(angleRad);
    }

    private javafx.geometry.Point2D calculateWorldMousePosition(double mouseSceneX, double mouseSceneY) {
        double worldMouseX = mouseSceneX - gamePane.getTranslateX();
        double worldMouseY = mouseSceneY - gamePane.getTranslateY();
        return new javafx.geometry.Point2D(worldMouseX, worldMouseY);
    }

    private double calculateRotationAngle(javafx.geometry.Point2D worldMousePosition) {
        double playerCenterX = playerModel.getX() + playerModel.getWidth() / 2;
        double playerCenterY = playerModel.getY() + playerModel.getHeight() / 2;

        return Math.atan2(worldMousePosition.getY() - playerCenterY, worldMousePosition.getX() - playerCenterX);
    }

    private void updateRotationAngle(double angleRad) {
        setRotate(Math.toDegrees(angleRad));
        playerModel.setAngle(angleRad);
    }

    public javafx.geometry.Point2D getProjectileSpawnPoint() {
        javafx.geometry.Point2D playerCenter = calculatePlayerCenter();
        javafx.geometry.Point2D globalOffset = calculateProjectileOffset();

        return playerCenter.add(globalOffset);
    }

    private javafx.geometry.Point2D calculatePlayerCenter() {
        double centerX = getLayoutX() + getTranslateX() + getFitWidth() / 2.0;
        double centerY = getLayoutY() + getTranslateY() + getFitHeight() / 2.0;
        return new javafx.geometry.Point2D(centerX, centerY);
    }

    private javafx.geometry.Point2D calculateProjectileOffset() {
        double angle = Math.toRadians(getRotate());
        double localOffsetX = getFitWidth() / 2.0;
        double localOffsetY = getFitHeight() / 3.0;

        double globalOffsetX = localOffsetX * Math.cos(angle) - localOffsetY * Math.sin(angle);
        double globalOffsetY = localOffsetX * Math.sin(angle) + localOffsetY * Math.cos(angle);

        return new javafx.geometry.Point2D(globalOffsetX, globalOffsetY);
    }

    public Player getPlayerModel() {
        return playerModel;
    }
}