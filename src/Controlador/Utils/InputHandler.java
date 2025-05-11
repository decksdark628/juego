package Controlador.Utils;

import Controlador.Entities.Player;
import Controlador.Projectiles.PlayerProjectile;
import Controlador.Entities.PlayerSprite;
import Controlador.Projectiles.ProjectileView;
import Controlador.SceneControllers.MainGameScreenController;
import Controlador.GameEngine;


import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

public class InputHandler {

    private double dx = 0;
    private double dy = 0;
    private boolean isReloading = false;

    private Player player;
    private PlayerSprite playerView;
    private GameEngine gameEngine;
    private AnchorPane rootPane;
    private MainGameScreenController mainGameScreenController;
    private UIManager uiManager;
    private SoundManager soundManager;

    public InputHandler(Player player, PlayerSprite playerView, GameEngine gameEngine, AnchorPane rootPane, MainGameScreenController mainGameScreenController, UIManager uiManager) { 
        this.player = player;
        this.playerView = playerView;
        this.gameEngine = gameEngine;
        this.rootPane = rootPane;
        this.mainGameScreenController = mainGameScreenController;
        this.uiManager = uiManager;
        this.soundManager = new SoundManager(); 
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

    private void handleKeyPressed(KeyEvent e) {
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
                mainGameScreenController.pauseGame();
                break;
            case R: 
                if(!player.isReloading()){
                player.reload();
                    if(player.isReloading()){
                    soundManager.playPlayerReloading(player.getReloadTime()); 
                    }
                }
                break;
            case TAB: 
                mainGameScreenController.showUpgradeMenu();
                break;
            default:
                break;
        }
        e.consume();
    }

    private void handleKeyReleased(KeyEvent e) {
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
        if (player.canShoot()) { 
            Point2D spawn = playerView.getProjectileSpawnPoint();
            Point2D target = calculateWorldCoordinates(mouseX, mouseY);
    
            PlayerProjectile proj = createPlayerProjectile(spawn, target);
            ProjectileView view = new ProjectileView(proj, true);
            proj.setView(view);
            gameEngine.addPlayerProjectile(proj, view);
    
            uiManager.updateAmmunitionText(); 
            soundManager.playPlayerShooting();
            isReloading = false;
        } 
        else if (!player.canShoot()) {
            if(!isReloading) {   
                soundManager.playPlayerReloading(player.getReloadTime());
                isReloading = true;
            }
        }       
    }

    private Point2D calculateWorldCoordinates(double mouseX, double mouseY) {
        double worldX = mouseX - rootPane.getTranslateX();
        double worldY = mouseY - rootPane.getTranslateY();
        return new Point2D(worldX, worldY);
    }

    private PlayerProjectile createPlayerProjectile(Point2D spawn, Point2D target) {
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
