package Controlador.SceneControllers;

import Controlador.Login;
import Controlador.Utils.Database;
import static Controlador.Utils.Database.dbConnection;
import Controlador.Utils.PopupWindows;
import Controlador.Utils.PopupWindows.AlertMessage;
import Controlador.Utils.SceneManager;
import Modelo.Config;
import Modelo.ImagePaths;
import Modelo.PlayerSessionData;
import Modelo.ScenePaths;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import static utilidades.bbdd.Bd.consultaSelect;
import static utilidades.bbdd.Bd.consultaModificacion;
import static utilidades.bbdd.Bd.importar;

public class LoginScreenController implements Initializable {
    private static final boolean LOG_IN = false;
    private static final boolean SIGN_UP = true;
    private boolean signUpOrLogIn = LOG_IN;
    
    @FXML
    private TextField inputUsernameField;
    @FXML
    private ImageView logInButton;
    @FXML
    private PasswordField inputPasswordField;
    @FXML
    private Hyperlink btnRegisterNewAccount;
    @FXML
    private Text msgSuccessfulSignUp;
    @FXML
    private Label txtRegisterInvitation;
    @FXML
    private ImageView dbStatusIndicator;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Database.init();
        if (Database.getTablesFound())
            dbStatusIndicator.setImage(new Image(getClass().getResourceAsStream(ImagePaths.DB_CONNECTED)));
        else
            dbStatusIndicator.setImage(new Image(getClass().getResourceAsStream(ImagePaths.DB_CONNECTION_ERROR)));
    }
        
    // methods //
    private void switchToLogInMode(){
        logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.LOGIN_BUTTON)));
        txtRegisterInvitation.setText("¿Aún no tienes una cuenta?");
        txtRegisterInvitation.setLayoutX(648);
        btnRegisterNewAccount.setText("Regístrate aquí");
        btnRegisterNewAccount.setLayoutX(679);
        signUpOrLogIn = LOG_IN;
    }
    
    private void switchToSignUpMode(){
        logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.SIGNUP_BUTTON)));
        msgSuccessfulSignUp.setVisible(false);
        txtRegisterInvitation.setLayoutX(663);
        txtRegisterInvitation.setText("¿Ya tienes una cuenta?");
        btnRegisterNewAccount.setLayoutX(691);
        btnRegisterNewAccount.setText("Inicia sesión");
        signUpOrLogIn = SIGN_UP;
    }
    
    // FXML interactions //
    @FXML
    private void buttonRelease(MouseEvent event) throws NoSuchAlgorithmException{
        String hashedPassword = Login.encryptPassword(inputPasswordField.getText());
        String usernameInput = inputUsernameField.getText();
        
        if (!Login.validUsername(usernameInput))
            PopupWindows.showSimpleAlert(AlertMessage.USR_NOT_VALID);
        
        else if (signUpOrLogIn == LOG_IN){
            if (!Login.userExists(usernameInput))
                PopupWindows.showSimpleAlert(AlertMessage.USR_DOESNT_EXIST);
            else{
                String[][] dbReturn = consultaSelect(Database.getDbConnection(), "select count (*) from usuarios where alias = '" + usernameInput + "' and contraseña = '" + hashedPassword + "'");
                if (dbReturn[0][0].equals("0"))
                    PopupWindows.showSimpleAlert(AlertMessage.WRONG_CREDENTIALS);
                else{
                    Login.setSuccessfulLogin(true);
                    PlayerSessionData.setLocalUsername(usernameInput);
                    SceneManager.goToScene(ScenePaths.GAME_SCREEN);
                }
            }
        }
        
        else if (signUpOrLogIn == SIGN_UP){
            if (Login.userExists(usernameInput))
                PopupWindows.showSimpleAlert(AlertMessage.USR_ALREADY_EXIST);
            else{
                if(!consultaModificacion(Database.getDbConnection(), "insert into usuarios (alias, contraseña, nivel) values ('" + usernameInput + "', '" + hashedPassword + "', 0)"))
                    PopupWindows.showSimpleAlert(AlertMessage.UNKWNOWN_ERROR);
                else{
                    msgSuccessfulSignUp.setVisible(true);
                    Login.setSuccessfulSignUp(true);
                    switchToLogInMode();
                }
            }
        }
        
        if (signUpOrLogIn == LOG_IN || (signUpOrLogIn == SIGN_UP && Login.getSuccessfulSignUp() == true))
            logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.LOGIN_BUTTON)));
    }

    @FXML
    private void registerButtonClicked(MouseEvent event) {
        if (signUpOrLogIn == LOG_IN){
            switchToSignUpMode();
        }
        else{
            switchToLogInMode();
        }
    }
    
    @FXML
    private void databaseIconClicked(MouseEvent event) {
        if (!Database.getTablesFound()){
            if((PopupWindows.showYesNoAlert(PopupWindows.AlertMessage.TABLES_NOT_CREATED)).get() == ButtonType.YES){
                importar(Config.CREATE_DB_TABLES, Database.getDbConnection());
                PopupWindows.showSimpleAlert(PopupWindows.AlertMessage.DATABASE_CREATED);
            }
        }        
        else{
            if (Config.ASK_TO_IMPORT_SAMPLE_DATA){            
                if((PopupWindows.showYesNoAlert(PopupWindows.AlertMessage.OFFER_SAMPLE_DATA)).get() == ButtonType.YES)
                    importar(Config.CREATE_DB_TABLES, dbConnection);
                    importar(Config.SAMPLE_DATA_PATH, dbConnection);
            }
            else{
                PopupWindows.showSimpleAlert(PopupWindows.AlertMessage.DATABASE_CONNECTED);
            }
        }
    }
    
    @FXML
    private void buttonHover(MouseEvent event) {
        if (signUpOrLogIn == LOG_IN)
            logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.LOGIN_BUTTON_HOVER)));
        else
            logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.SIGNUP_BUTTON_HOVER)));
        
    }
    @FXML
    private void buttonLeaveHover(MouseEvent event) {
        if (signUpOrLogIn == LOG_IN)
            logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.LOGIN_BUTTON)));
        else 
            logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.SIGNUP_BUTTON)));
    }   
    @FXML
    private void buttonPress(MouseEvent event) {
        if (signUpOrLogIn == LOG_IN)
            logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.LOGIN_BUTTON_PRESSED)));
        else
            logInButton.setImage(new Image(getClass().getResourceAsStream(ImagePaths.SIGNUP_BUTTON_PRESSED)));
    }
}