package Controlador.SceneControllers;

import Controlador.Cursor;
import Controlador.GameEngine;
import Controlador.Utils.SoundManager;
import Modelo.ScenePaths;
import Controlador.Utils.BackgroundMusic;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import java.io.IOException;

public class PauseController {
    @FXML private Button resumeButton;
    @FXML private Button exitButton;
    @FXML private Button upgradeMenuButton;

    private Stage stage;
    private Scene gameScene;
    private AnimationTimer gameLoop;
    private Timeline gameTimer;
    private Timeline enemySpawner;
    private GameEngine gameEngine;
    private BackgroundMusic backgroundMusic;
    private BackgroundMusic menuMusic;

    public void initialize() {
        setupResumeButton();
        setupUpgradeMenuButton();
        setupExitButton();
    }

    private void setupResumeButton() {
        resumeButton.setOnAction(event -> {
            new SoundManager().playButton();
            resumeGame();
        });
    }

    private void setupExitButton() {
        exitButton.setOnAction(event -> {
            new SoundManager().playButton();
            exitGame();
        });
    }

    private void setupUpgradeMenuButton() {
        upgradeMenuButton.setOnAction(event -> {
            new SoundManager().playButton();
            openUpgradeMenu();
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }    

    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    public void setGameTimers(AnimationTimer gameLoop, Timeline gameTimer, Timeline enemySpawner) {
        this.gameLoop = gameLoop;
        this.gameTimer = gameTimer;
        this.enemySpawner = enemySpawner;
    }

    public void setBackgroundMusic(BackgroundMusic backgroundMusic) {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
        this.backgroundMusic = backgroundMusic;


        menuMusic = BackgroundMusic.getMenuMusic();
        menuMusic.play();
    }

    @FXML
    private void resumeGame() {
        resumeGameLoop();
        resumeEnemySpawner();
        resumeGameTimer();

        if (menuMusic != null) {
            menuMusic.pause();
        }
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
        switchToGameScene();
    }

    private void resumeGameLoop() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    private void resumeEnemySpawner() {
        if (enemySpawner != null) {
            enemySpawner.play();
        }
    }

    private void resumeGameTimer() {
        if (gameTimer != null) {
            gameTimer.play(); 
        }
    }

    private void switchToGameScene() {
        stage.setScene(gameScene);
        Cursor.applyCustomCursor(gameScene);
    }

    @FXML
    private void exitGame() {
        closeStage();
    }

    private void closeStage() {
        if (stage != null) {
            stage.close();
        }
    }

    private void openUpgradeMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ScenePaths.UPGRADE_MENU));
            Parent upgradeRoot = loader.load();

            UpgradeMenuController controller = loader.getController();

            controller.setPlayer(gameEngine.getPlayer()); 
            controller.setStage(stage);
            controller.setGameScene(gameScene);
            controller.setGameTimers(gameLoop, gameTimer, enemySpawner);
            controller.setBackgroundMusic(backgroundMusic);

            Scene upgradeScene = new Scene(upgradeRoot, gameScene.getWidth(), gameScene.getHeight());
            Cursor.applyCustomCursor(upgradeScene);

            stage.setScene(upgradeScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}