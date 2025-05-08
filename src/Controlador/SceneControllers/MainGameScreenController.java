package Controlador.SceneControllers;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.control.ProgressBar;

import Modelo.GameConstants;
import Controlador.Cursor;
import Controlador.EnemySpawner;
import Controlador.GameEngine;
import Controlador.Utils.Database;
import Controlador.Utils.UIManager;
import Controlador.Utils.InputHandler;
import Controlador.Utils.SceneManager;
import Controlador.Utils.TimestampGenerator;
import Modelo.ImagePaths;
import Modelo.PlayerSessionData;
import Modelo.ScenePaths;


import java.io.IOException;


public class MainGameScreenController {
    
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane gamePane;
    @FXML private TilePane backgroundTiles;
    @FXML private Text HPText;
    @FXML private VBox healthBarContainer;
    @FXML private Rectangle healthBar;
    @FXML private Rectangle healthBarBackground;
    @FXML private Text timerText;
    @FXML private Text scoreText;
    @FXML private StackPane uiContainer;
    @FXML private Text levelText;
    @FXML private ProgressBar levelProgressBar;
    @FXML private ProgressBar ammunitionBar;
    @FXML private Text ammunitionText; 
    @FXML private Text skillPointText;

    private GameEngine gameEngine;
    private InputHandler inputHandler;
    private EnemySpawner enemySpawner;
    private UIManager uiManager;

    private AnimationTimer gameLoop;
    private Timeline gameTimer;
    private Timeline enemySpawnerTimeline;

    public void initialize() {
        initializeBackground();
        initializeGameComponents();
        initializeTimers();
        requestFocusOnRootPane();
    }

    private void initializeGameComponents() {
        initializeGameEngine();
        initializeUIManager();
        initializeInputHandler();
        initializeEnemySpawner();
    }

    private void initializeBackground() {
        try {
            javafx.scene.image.Image tileImage = new javafx.scene.image.Image(getClass().getResourceAsStream(ImagePaths.GROUND_TEXTURE));
            int tilesX = (int) Math.ceil(GameConstants.MAP_WIDTH / GameConstants.TILE_SIZE);
            int tilesY = (int) Math.ceil(GameConstants.MAP_HEIGHT / GameConstants.TILE_SIZE);
            backgroundTiles.getChildren().clear();
            for (int y = 0; y < tilesY; y++) {
                for (int x = 0; x < tilesX; x++) {
                    javafx.scene.image.ImageView tile = new javafx.scene.image.ImageView(tileImage);
                    tile.setFitWidth(GameConstants.TILE_SIZE);
                    tile.setFitHeight(GameConstants.TILE_SIZE);
                    tile.setX(x * GameConstants.TILE_SIZE);
                    tile.setY(y * GameConstants.TILE_SIZE);
                    backgroundTiles.getChildren().add(tile);
                }
            }
        } catch (Exception e) {
            backgroundTiles.setStyle("-fx-background-color: #2D2D2D;");
        }
    }

     private void initializeGameEngine() {
        gameEngine = new GameEngine(rootPane);
    }

    private void initializeUIManager() {
        uiManager = new UIManager(gameEngine.getPlayer(), HPText, scoreText, timerText,
                healthBar, healthBarBackground, healthBarContainer, uiContainer, levelText, levelProgressBar, ammunitionText, ammunitionBar, skillPointText);
    }

    private void initializeInputHandler() {
        inputHandler = new InputHandler(gameEngine.getPlayer(), gameEngine.getPlayerView(), gameEngine, rootPane, this, uiManager);
    }

    private void initializeEnemySpawner() {
        enemySpawner = new EnemySpawner(gameEngine.getEnemies(), gameEngine.getEnemyProjectiles(), gameEngine);
    }

    private void initializeTimers() {
        setupGameTimer();
        setupGameLoop();
        startEnemySpawner();
    }

    private void requestFocusOnRootPane() {
        Platform.runLater(() -> rootPane.requestFocus());
    }

    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
            }
        };
        gameLoop.start();
    }

    private void setupGameTimer() {
        gameTimer = new Timeline(new javafx.animation.KeyFrame(Duration.seconds(1), e -> {
            gameEngine.incrementElapsedSeconds();
            uiManager.updateUI(gameEngine.getElapsedSeconds());
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void startEnemySpawner() {
        enemySpawnerTimeline = new Timeline(new javafx.animation.KeyFrame(Duration.seconds(1), e -> {
            enemySpawner.spawnEnemies(gameEngine.getElapsedSeconds());
        }));
        enemySpawnerTimeline.setCycleCount(Timeline.INDEFINITE);
        enemySpawnerTimeline.play();
    }

    private void updateGame() {
        updatePlayerMovement();
        updateCamera();
        updateEntities();
        checkCollisions();
        updateUI();
        checkGameOver();
        //checkLvlUp();
    }

    private void updatePlayerMovement() {
        gameEngine.updatePlayerMovement(inputHandler.getDx(), inputHandler.getDy());
    }

    private void updateEntities() {
        gameEngine.updateEnemies();
        gameEngine.updateProjectiles();
    }

    private void checkCollisions() {
        gameEngine.checkCollisions();
    }

    private void updateUI() {
        updatePlayerUI();
    }

    private void checkGameOver() {
        if (gameEngine.getPlayer().isDead()) {
            gameOver();
        }
    }
    
    /*private void checkLvlUp() {
        if (gameEngine.getPlayer() != null && gameEngine.getPlayer().hasSkillPointsToSpend()){
            showUpgradeMenu();
        }
    }*/

    private void updatePlayerUI() {
        uiManager.updateUI(gameEngine.getElapsedSeconds());

        double playerX = gameEngine.getPlayer().getX();
        double playerY = gameEngine.getPlayer().getY();
        double healthBarWidth = healthBarContainer.getWidth(); 


        double healthBarX = playerX - healthBarWidth / 2;
        double healthBarY = playerY + 50; 

        healthBarContainer.setLayoutX(healthBarX);
        healthBarContainer.setLayoutY(healthBarY);
        gameEngine.regeneratePlayerHp();
    }

    private void updateCamera() {
        centerViewOnPlayer();
    }

    private void centerViewOnPlayer() {
        if (gameEngine.getPlayer() == null || rootPane == null || rootPane.getScene() == null)
            return;

        double centerX = calculateCenterX();
        double centerY = calculateCenterY();

        updateCameraPosition(centerX, centerY);
        updateUIPosition(centerX, centerY);
    }

    private double calculateCenterX() {
        double sceneWidth = rootPane.getScene().getWidth();
        double playerX = gameEngine.getPlayer().getX();
        return Math.min(Math.max(playerX - sceneWidth / 2, 0), GameConstants.MAP_WIDTH - sceneWidth);
    }

    private double calculateCenterY() {
        double sceneHeight = rootPane.getScene().getHeight();
        double playerY = gameEngine.getPlayer().getY();
        return Math.min(Math.max(playerY - sceneHeight / 2, 0), GameConstants.MAP_HEIGHT - sceneHeight);
    }

    private void updateCameraPosition(double centerX, double centerY) {
        rootPane.setTranslateX(-centerX);
        rootPane.setTranslateY(-centerY);
    }

    private void updateUIPosition(double centerX, double centerY) {
        uiContainer.setTranslateX(centerX);
        uiContainer.setTranslateY(centerY);
    }

    public void pauseGame() {
        stopGameTimers();
        loadPauseMenu();
    }

    private void stopGameTimers() {
        gameLoop.stop();
        gameTimer.stop();
        if (enemySpawnerTimeline != null) enemySpawnerTimeline.stop();
    }

    private void loadPauseMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ScenePaths.PAUSE_MENU));
            Parent root = loader.load();
            PauseController controller = loader.getController();
            
            controller.setGameEngine(gameEngine); //añadi esto porque sino me daba null pointer exception al entrar al menu de mejoras
            controller.setStage((Stage) rootPane.getScene().getWindow());
            controller.setGameScene(rootPane.getScene());
            controller.setGameTimers(gameLoop, gameTimer, enemySpawnerTimeline);

            Scene pauseScene = new Scene(root, 1024, 768);
            Cursor.applyCustomCursor(pauseScene); 
            ((Stage) rootPane.getScene().getWindow()).setScene(pauseScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameOver() {
        PlayerSessionData.setLocalScore(gameEngine.getPlayer().getScore());
        PlayerSessionData.setDeathTimestamp(TimestampGenerator.generate());
        Database.sendPlayerScore();
        stopGame();
        loadGameOverScene();
    }

    private void stopGame() {
        gameLoop.stop();
        gameTimer.stop();
        if (enemySpawnerTimeline != null) enemySpawnerTimeline.stop();
    }

    private void loadGameOverScene() {
        SceneManager.goToScene(ScenePaths.GAME_OVER);
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(ScenePaths.GAME_OVER));
//            Parent root = loader.load();
//            Scene2Controller controller = loader.getController();
//            controller.setFinalScore(gameEngine.getPlayer().getScore());
//            controller.setPrimaryStage((Stage) rootPane.getScene().getWindow());
//
//            Scene gameOverScene = new Scene(root, 1024, 768);
//            Cursor.applyCustomCursor(gameOverScene); 
//            ((Stage) rootPane.getScene().getWindow()).setScene(gameOverScene);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    
     private void loadUpgradeMenu() {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ScenePaths.UPGRADE_MENU));
            Parent upgradeRoot = loader.load();
            UpgradeMenuController controller = loader.getController(); 

            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            Scene currentGameScene = rootPane.getScene();

            controller.setPlayer(gameEngine.getPlayer()); 
            controller.setStage(currentStage);            
            controller.setGameScene(currentGameScene);   
            

            controller.setGameTimers(gameLoop, gameTimer, enemySpawnerTimeline);

            Scene upgradeScene = new Scene(upgradeRoot, currentGameScene.getWidth(), currentGameScene.getHeight()); // Usa las dimensiones actuales
            Cursor.applyCustomCursor(upgradeScene); 
            currentStage.setScene(upgradeScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showUpgradeMenu() {
        stopGameTimers();
        loadUpgradeMenu();
    }     
}