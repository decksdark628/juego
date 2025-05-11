package Controlador.SceneControllers;

import Controlador.Cursor;
import Controlador.Utils.BackgroundMusic;
import Controlador.Utils.SoundManager;
import Modelo.ScenePaths;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class AltGameOverController {
    @FXML private Text scoreText;
    @FXML private Button restartButton;
    @FXML private Button exitButton;

    private Stage primaryStage;
    private BackgroundMusic gameOverMusic;

    public void initialize() {
        setupRestartButton();
        setupExitButton();
    }

    private void setupRestartButton() {
        restartButton.setOnAction(event -> {
            new SoundManager().playButton();
            restartGame();
        });
    }

    private void setupExitButton() {
        exitButton.setOnAction(event -> {
            new SoundManager().playButton();
            exitGame();
        });
    }

    public void setFinalScore(int score) {
        scoreText.setText("Puntuación Final: " + score);
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setGameOverMusic(BackgroundMusic music) {
        this.gameOverMusic = music;
        if (gameOverMusic != null) {
            gameOverMusic.play();
        }
    }

    @FXML
    private void restartGame() {
        try {
            Scene gameScene = loadGameScene();
            applyCustomCursor(gameScene);
            switchToScene(gameScene);
            if (gameOverMusic != null) {
                gameOverMusic.stop();
            }
        } catch (Exception e) {
            handleSceneLoadingError(e);
        }
    }

    private Scene loadGameScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ScenePaths.JULIANS_GAME_OVER));
        Parent root = loader.load();
        return new Scene(root, 1024, 768);
    }

    private void applyCustomCursor(Scene scene) {
        Cursor.applyCustomCursor(scene);
    }

    private void switchToScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleSceneLoadingError(Exception e) {
        e.printStackTrace();
    }

    @FXML
    private void exitGame() {
        exitApplication();
    }

    private void exitApplication() {
        if (gameOverMusic != null) {
            gameOverMusic.stop();
        }
        Platform.exit();
    }
}