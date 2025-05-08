package Controlador.Utils;

import Controlador.GameEngine;
import Controlador.SceneControllers.MainGameScreenController;
import Controlador.Entities.Player;
import Controlador.Projectiles.PlayerProjectile;
import Controlador.Entities.PlayeSprite;
import Controlador.Projectiles.ProjectileView;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

public class InputHandler {

    private double dx = 0;
    private double dy = 0;

    private Player player;
    private PlayeSprite playerView;
    private GameEngine gameEngine;
    private AnchorPane rootPane;
    private MainGameScreenController gameController;
    private UIManager uiManager;

    public InputHandler(Player player, PlayeSprite playerView, GameEngine gameEngine, AnchorPane rootPane, MainGameScreenController gameController, UIManager uiManager) { 
        this.player = player;
        this.playerView = playerView;
        this.gameEngine = gameEngine;
        this.rootPane = rootPane;
        this.gameController = gameController;
        this.uiManager = uiManager;
        setupInputHandlers();
    }

    private void setupInputHandlers() {
        setupKeyboardHandlers();
        setupMouseHandlers();
        setupFocus();
    }

    private void setupKeyboardHandlers() {
        rootPane.setOnKeyPressed(this::handleKeyPressed);
        rootPane.setOnKeyReleased(this::handleKeyReleased);
    }

    private void setupMouseHandlers() {
        rootPane.setOnMouseMoved(e -> playerView.rotatePlayer(e.getSceneX(), e.getSceneY()));
        rootPane.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                handleMouseClick(e.getSceneX(), e.getSceneY());
            }
        });
    }

    private void setupFocus() {
        rootPane.setFocusTraversable(true);
        rootPane.requestFocus();
    }

    private void handleKeyPressed(javafx.scene.input.KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case W:
                dy = -1;
                break;
            case S:
                dy = 1;
                break;
            case A:
                dx = -1;
                break;
            case D:
                dx = 1;
                break;
            case SPACE:
                gameController.pauseGame();
                break;
                case R: 
                player.reload();
                uiManager.updateAmmunitionText();
                break;                
            default:
                break;
        }
        e.consume();
    }

    private void handleKeyReleased(javafx.scene.input.KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case W:
                if (dy < 0) dy = 0;
                break;
            case S:
                if (dy > 0) dy = 0;
                break;
            case A:
                if (dx < 0) dx = 0;
                break;
            case D:
                if (dx > 0) dx = 0;
                break;
            default:
                break;
        }
        e.consume();
    }


    private void handleMouseClick(double mouseX, double mouseY) {
        if (player.Canshoot()) { 
            javafx.geometry.Point2D spawn = playerView.getProjectileSpawnPoint();
            javafx.geometry.Point2D target = calculateWorldCoordinates(mouseX, mouseY);
    
            PlayerProjectile proj = createPlayerProjectile(spawn, target);
            ProjectileView view = new ProjectileView(proj, true);
            proj.setView(view);
            gameEngine.addPlayerProjectile(proj, view);
    
            uiManager.updateAmmunitionText(); 
        } 
    }

    private javafx.geometry.Point2D calculateWorldCoordinates(double mouseX, double mouseY) {
        double worldX = mouseX - rootPane.getTranslateX();
        double worldY = mouseY - rootPane.getTranslateY();
        return new javafx.geometry.Point2D(worldX, worldY);
    }

    private PlayerProjectile createPlayerProjectile(javafx.geometry.Point2D spawn, javafx.geometry.Point2D target) {
        return new PlayerProjectile(
            spawn.getX(),
            spawn.getY(),
            target.getX(),
            target.getY(),
            player.getDamage()
        );
    }

    public double getDx() { return dx;}
    public double getDy() { return dy;}
}