package Controlador.SceneControllers;

import Controlador.Utils.DataImporter;
import static Controlador.Utils.DataImporter.loadComentarios;
import Controlador.Utils.Database;
import Controlador.Utils.SceneManager;
import Modelo.Partida;
import Modelo.Comentario;
import Modelo.PlayerSessionData;
import Modelo.ScenePaths;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import static utilidades.bbdd.Bd.consultaModificacion;

public class ScoreBoardController {
    private int selectedPartidaID;

    @FXML
    private ImageView btnLogoff;

    @FXML
    private ImageView btnPublish;

    @FXML
    private ImageView btnReturn;
    
    @FXML
    private TableView<Partida> scoresTable;

    @FXML
    private TableColumn<Partida, Integer> columnID;

    @FXML
    private TableColumn<Partida, Integer> columnScore;

    @FXML
    private TableColumn<Partida, String> columnUserName;
    
    @FXML
    private VBox commentsContainer;
    
    @FXML
    private ScrollPane commentsScrollpane;


    @FXML
    private TextArea commentBox;
    
    //cambios:
    //playersessiondata: remover localpartida porque no se usa. SQL auto añade la siguiente partida
    // init.sql  y el otro script: corregidos para que se pueda autoincrementar entradas
    // Scoreboard controller: ACTUALIZAR LA TABLA AL DEJAR EL COMENTARIO
    // playerSessionData, GameOverScreen, gamecontroller, timestampgenerator: timestamp del momento de muerte
    
    public void initialize(){
        scoresTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        columnUserName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getAlias()));
        
        scoresTable.setItems(FXCollections.observableArrayList(DataImporter.getPartidas()));
        
        scoresTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null){
                int id = newSelection.getId();
                selectedPartidaID = id;
                loadComments(DataImporter.getComentarios());
            }
        });
    }
    
    public void loadComments(ArrayList<Comentario> comentarios){

        commentsContainer.getChildren().clear();
        
        for (Comentario comentario: comentarios){
            if (comentario.getPartida().getId() == selectedPartidaID){
                VBox commentBubble = new VBox();
                commentBubble.getStyleClass().add("comment-bubble");
            
                HBox aliasAndLevelBundle = new HBox();
                aliasAndLevelBundle.getStyleClass().add("alias-and-level-bundle");
                
                Label userAlias = new Label(comentario.getUser().getAlias());
                userAlias.getStyleClass().add("user-alias-label");
                
                Label userLevel = new Label("["+comentario.getUser().translateLevelToText()+"]");
                userLevel.getStyleClass().add("level-tag");
                
                Label commentText = new Label(comentario.getText());
                commentText.getStyleClass().add("comment-text");
                
                aliasAndLevelBundle.getChildren().addAll(userAlias, userLevel);
                commentBubble.getChildren().addAll(aliasAndLevelBundle, commentText);
                commentsContainer.getChildren().add(commentBubble);
            }   
        }
    }
    
    // Buttons
    @FXML
    void LogOffBtnHover(MouseEvent event) {
        btnLogoff.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/logOffButton-hover.png")));
    }

    @FXML
    void LogOffBtnLeft(MouseEvent event) {
        btnLogoff.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/logOffButton.png")));
    }

    @FXML
    void LogOffBtnPressed(MouseEvent event) {
        btnLogoff.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/logOffButton-pressed.png")));
        PlayerSessionData.setLocalUsername(null);
        PlayerSessionData.setLocalScore(0);
        SceneManager.goToScene(ScenePaths.LOGIN_SCREEN);
    }
    
    @FXML
    void LogOffBtnRelease(MouseEvent event) {
        btnPublish.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/logOffButton-hover.png")));
    }

    @FXML
    void publishBtnHover(MouseEvent event) {
        btnPublish.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/publishButton-hover.png")));
    }

    @FXML
    void publishBtnLeft(MouseEvent event) {
        btnPublish.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/publishButton.png")));
    }

    @FXML
    void publishBtnPressed(MouseEvent event) {
        btnPublish.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/publishButton-pressed.png")));
    }
    
    @FXML
    void publishBtnRelease(MouseEvent event) {
        btnPublish.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/publishButton-hover.png")));
        if(!(commentBox.getText().trim().isEmpty())){
            consultaModificacion(Database.getDbConnection(), "insert into comentarios (texto, id_partida, alias_usuario) values ('" + commentBox.getText().trim() + "', '" + selectedPartidaID + "', '" + PlayerSessionData.getLocalUsername() + "')");
            commentBox.clear();
            
            loadComentarios();
            loadComments(DataImporter.getComentarios());
        }
    }
    
    @FXML
    void returnBtnHover(MouseEvent event) {
        btnReturn.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/returnToGameButton-hover.png")));
    }

    @FXML
    void returnBtnLeft(MouseEvent event) {
        btnReturn.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/returnToGameButton.png")));
    }

    @FXML
    void returnBtnPressed(MouseEvent event) {
        btnReturn.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/returnToGameButton-pressed.png")));
    }
    
    @FXML
    void returnBtnRelease(MouseEvent event) {
        btnReturn.setImage(new Image(getClass().getResourceAsStream("/Vista/assets/images/returnToGameButton-hover.png")));
        SceneManager.goToScene(ScenePaths.GAME_SCREEN);
    }
}
