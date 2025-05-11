package Controlador.SceneControllers;

import Controlador.Utils.BackgroundMusic;
import Controlador.Utils.DataImporter;
import Controlador.Utils.SceneManager;
import Modelo.PlayerSessionData;
import Modelo.ScenePaths;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GameOverController implements Initializable{
    private Stage primaryStage;
    private static BackgroundMusic gameOverMusic;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setScore(PlayerSessionData.getLocalScore());
        
        Glow glow = new Glow();
        glow.setLevel(0.8);
        labelPuntaje.setEffect(glow);
        labelTerindes.setEffect(glow);

        DataImporter.init();
    }

    public void setScore(int score) {
        labelPuntaje.setText("PUNTUACION: " + score);
    }

    @FXML
    private ImageView btnGoToScoreboard;

    @FXML
    private ImageView btnPlayAgain;

    @FXML
    private Label labelPuntaje;

    @FXML
    private Label labelTerindes;
    

    @FXML
    void playAgainMouseEnter(MouseEvent event) {
        btnPlayAgain.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/playAgainButton-hover.png")));
    }

    @FXML
    void playAgainMouseLeave(MouseEvent event) {
        btnPlayAgain.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/playAgainButton.png")));
    }

    @FXML
    void playAgainMousePressed(MouseEvent event) {
        btnPlayAgain.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/playAgainButton-pressed.png")));
    }

    @FXML
    void playAgaincRelease(MouseEvent event) {
        btnPlayAgain.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/playAgainButton-hover.png")));
        SceneManager.goToScene(ScenePaths.GAME_SCREEN);
    }

    @FXML
    void seeScoresHover(MouseEvent event) {
        btnGoToScoreboard.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/seeScoreboardButton-hover.png")));
    }

    @FXML
    void seeScoresLeave(MouseEvent event) {
        btnGoToScoreboard.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/seeScoreboardButton.png")));
    }

    @FXML
    void seeScoresPressed(MouseEvent event) {
        btnGoToScoreboard.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/seeScoreboardButton-pressed.png")));
    }

    @FXML
    void seeScoresRelease(MouseEvent event) {
        btnGoToScoreboard.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/seeScoreboardButton-hover.png")));    
        SceneManager.goToScene(ScenePaths.SCORES);
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
    
    public static void stopGameOverMusic(){
        gameOverMusic.stop();
    }
}
