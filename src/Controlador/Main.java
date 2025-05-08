package Controlador;

import Modelo.Config;
import Controlador.Utils.SceneManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        SceneManager.initialize(primaryStage);
        
        primaryStage.setTitle(Config.GAME_TITLE);
        primaryStage.setResizable(false);

        SceneManager.goToScene(Config.getStartScenePath());
        primaryStage.show();
    }    
    
    public static void main(String[] args) {
        launch(args);
    }
}