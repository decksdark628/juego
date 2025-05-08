package Controlador.Utils;

import Controlador.Login;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PopupWindows {
    public enum AlertMessage{
        USR_NOT_VALID("Error", "El nombre de usuario no tiene un formato valido.\nAsegurate de remover caracteres especiales y que la longitud de tu nombre de usuario sea de por lo menos " + Login.getMIN_USER_LENGTH() + " carácteres"),
        USR_DOESNT_EXIST("No se pudo iniciar sesión", "El nombre de usuario proporcionado no existe en la base de datos"),
        USR_ALREADY_EXIST("No se pudo iniciar sesión", "El nombre de usuario proporcionado ya esta en uso."),
        WRONG_CREDENTIALS("No se pudo iniciar sesión", "Los datos proporcionados no son correctos"),
        TABLES_NOT_CREATED("Error en la base de datos", "Parece que tu base de datos está vacia o incompleta\n¿Deseas generar la base de datos desde cero? Cualquier información anterior será eliminada"),
        DATABASE_CONNECTED("Conectado a la base de datos", "La conexión con la base de datos es correcta. Puedes iniciar sesión o registrarte para empezar a jugar."),
        OFFER_SAMPLE_DATA("Conectado a la base de datos", "La conexión con la base de datos es correcta.\n¿Deseas importar usuarios y comentarios de ejemplo? Esto podría sobreescribir la información que ya existe en tu base de datos."),
        UNKWNOWN_ERROR("Error", "Algo ha salido mal"),
        DATABASE_NOT_CONNECTED("Error", "No se pudo realizar la conexión con la base de datos. Asegurate que una base de datos llamada 'juego' existe en tu sistema"),
        DATABASE_CREATED("Base de datos", "La base de datos ha sido reinciada con exito!");
        
        private final String title;
        private final String content;
        
        AlertMessage(String title, String content){
            this.title = title;
            this.content = content;
        }
    }
    
    public static void showSimpleAlert(AlertMessage message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(message.title);
        alert.setHeaderText(null);
        alert.setContentText(message.content);
        alert.showAndWait();
    }
    
    public static Optional<ButtonType> showYesNoAlert(AlertMessage message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle(message.title);
        alert.setHeaderText(null);
        alert.setContentText(message.content);
        
        return alert.showAndWait();
    }
}
