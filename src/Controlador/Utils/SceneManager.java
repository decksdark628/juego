
package Controlador.Utils;

import Modelo.Config;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class SceneManager {
    private static Stage mainStage;
    
    public static void initialize(Stage stage){
        mainStage = stage;
    }
    public static void goToScene(String path){
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(path));
            Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            mainStage.setScene(scene);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
