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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;

import Modelo.ImagePaths;
import Modelo.ScenePaths;
import Modelo.SoundPaths;
import Modelo.GameConstants;
import Controlador.Cursor;
import Controlador.GameEngine;
import Controlador.EnemySpawner;
import Controlador.Utils.BackgroundMusic;
import Controlador.Utils.Database;
import Controlador.Utils.InputHandler;
import Controlador.Utils.SoundManager;
import Controlador.Utils.TimestampGenerator;
import Controlador.Utils.UIManager;
import Modelo.PlayerSessionData;

import java.io.IOException;


public class MainGameScreenController {
    
    @FXML private AnchorPane rootPane;
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
    private UIManager uiManager;
    private SoundManager soundManager;
    private BackgroundMusic backgroundMusic;
    private EnemySpawner enemySpawner;

    private AnimationTimer gameLoop;
    private Timeline gameTimer;
    private Timeline enemySpawnerTimeline;
    
    private long lastFrameTime = 0;

    private BackgroundMusic gameOverMusic;

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
        initializeSoundManager();
        setBackgroundMusic();  
        
    }

    private void initializeBackground() {
        try {
            Image tileImage = new Image(getClass().getResourceAsStream(ImagePaths.GROUND_TEXTURE));
            int tilesX = (int) Math.ceil(GameConstants.MAP_WIDTH / GameConstants.TILE_SIZE);
            int tilesY = (int) Math.ceil(GameConstants.MAP_HEIGHT / GameConstants.TILE_SIZE);
            backgroundTiles.getChildren().clear();
            for (int y = 0; y < tilesY; y++) {
                for (int x = 0; x < tilesX; x++) {
                    ImageView tile = new ImageView(tileImage);
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
       enemySpawner = new EnemySpawner(/*gameEngine.getEnemies(), */gameEngine.getEnemyProjectiles(), gameEngine);
    }

    
    private void initializeSoundManager() {
        soundManager = new SoundManager();
    }

    private void initializeTimers() {
        setupGameTimer();
        setupGameLoop();
    }

    private void requestFocusOnRootPane() {
        Platform.runLater(() -> rootPane.requestFocus());
    }

    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }
                
                double deltaTime = (now - lastFrameTime) / 1_000_000_000.0; 
                lastFrameTime = now;
                
                if (deltaTime > 0.1) {
                    deltaTime = 0.1;
                }
                
                updateGame(deltaTime);
            }
        };
        gameLoop.start();
    }

    private void setupGameTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            gameEngine.incrementElapsedSeconds();
            uiManager.updateUI(gameEngine.getElapsedSeconds());
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void updateGame(double deltaTime) {
        updatePlayerMovement(deltaTime);
        updateCamera();
        updateEntities(deltaTime);
        checkCollisions();
        updateUI();
        checkGameOver();
        checkLvlUp();
        gameEngine.regeneratePlayerHp();
    }

    private void updatePlayerMovement(double deltaTime) {
        gameEngine.updatePlayerMovement(inputHandler.getDx(), inputHandler.getDy(), deltaTime);
    }

    private void updateEntities(double deltaTime) {
        gameEngine.updateEnemies(deltaTime);
        gameEngine.updateProjectiles(deltaTime);
    }

    private void checkCollisions() {
        gameEngine.checkCollisions();
    }

    private void updateUI() {
        updatePlayerUI();
    }

    private void checkGameOver() {
        if (gameEngine.getPlayer().isDead()) {
            soundManager.playPlayerDeath();
            gameOver();
        }
    }
    
    private void checkLvlUp() {
        if (gameEngine.getPlayer().hasLeveledUp()){
            soundManager.playPlayerLvlUp();
            gameEngine.getPlayerView().flashYellow();
        }
    }

    private void updatePlayerUI() {
        uiManager.updateUI(gameEngine.getElapsedSeconds());

        double playerX = gameEngine.getPlayer().getX();
        double playerY = gameEngine.getPlayer().getY();
        double healthBarWidth = healthBarContainer.getWidth(); 


        double healthBarX = playerX - healthBarWidth / 2;
        double healthBarY = playerY + 50; 

        healthBarContainer.setLayoutX(healthBarX);
        healthBarContainer.setLayoutY(healthBarY);
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
            controller.setStage((Stage) rootPane.getScene().getWindow());
            controller.setGameScene(rootPane.getScene());
            controller.setGameTimers(gameLoop, gameTimer, enemySpawnerTimeline);
            controller.setGameEngine(gameEngine);
            controller.setBackgroundMusic(backgroundMusic);

            Scene pauseScene = new Scene(root, 1024, 768);
            Cursor.applyCustomCursor(pauseScene); 
            ((Stage) rootPane.getScene().getWindow()).setScene(pauseScene);

            backgroundMusic.pause();
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ScenePaths.GAME_OVER));
            Parent root = loader.load();
            GameOverController controller = loader.getController();
            controller.setPrimaryStage((Stage) rootPane.getScene().getWindow());

            Scene gameOverScene = new Scene(root, 1024, 768);
            Cursor.applyCustomCursor(gameOverScene); 
            ((Stage) rootPane.getScene().getWindow()).setScene(gameOverScene);

            backgroundMusic.stop();
            gameOverMusic = new BackgroundMusic(SoundPaths.GAME_OVER_TRACK);
            controller.setGameOverMusic(gameOverMusic);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            controller.setBackgroundMusic(backgroundMusic);

            Scene upgradeScene = new Scene(upgradeRoot, currentGameScene.getWidth(), currentGameScene.getHeight()); 
            Cursor.applyCustomCursor(upgradeScene); 
            currentStage.setScene(upgradeScene);
            backgroundMusic.pause();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showUpgradeMenu() {
        stopGameTimers();
        loadUpgradeMenu();
    }

    public void setBackgroundMusic() {
        this.backgroundMusic = new BackgroundMusic();
        backgroundMusic.play();
    }

    public BackgroundMusic getBackgroundMusic() {
        return backgroundMusic;
    }   
}
