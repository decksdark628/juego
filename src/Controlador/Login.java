package Controlador;

import Controlador.Utils.Database;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import static utilidades.bbdd.Bd.consultaSelect;

public class Login {
    private static final int MIN_USER_LENGTH = 5;
    private static boolean successfulLogin = false;
    private static boolean successfulSignUp = false;

    
    public static int getMIN_USER_LENGTH() {
        return MIN_USER_LENGTH;
    }
    
    public static boolean getSuccessfulLogin() {
        return successfulLogin;
    }

    public static void setSuccessfulLogin(boolean successfulLogin) {
        Login.successfulLogin = successfulLogin;
    }

    public static boolean getSuccessfulSignUp() {
        return successfulSignUp;
    }

    public static void setSuccessfulSignUp(boolean successfulSignUp) {
        Login.successfulSignUp = successfulSignUp;
    }
    
    
    public static String encryptPassword(String passwordInput) throws NoSuchAlgorithmException{
        String hashedPassword;
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(passwordInput.getBytes(StandardCharsets.UTF_8));
        hashedPassword = DatatypeConverter.printHexBinary(digest).toLowerCase();
        
        return hashedPassword;
    }
    
    public static boolean validUsername(String usr){
        boolean valid = true;
        char c;
        
        if (usr.length() < MIN_USER_LENGTH)
            valid = false;
        for (int i =  0; i<usr.length() && valid; i++){
            c = usr.charAt(i);
            if (  (  (c >= '0' && c <= '9')  ||  (c>= 'A' && c<= 'Z')  ||  (c>= 'a' && c<= 'z')  ||  c == '_'  ||  c == '-') == false ) 
                valid = false;
        }
        return valid;
    }
    
    public static boolean userExists(String usr){
        boolean exists = false;
        String[][] dbReturn = consultaSelect(Database.getDbConnection(), "select count (*) from usuarios where alias = '" + usr + "'");
        if (dbReturn[0][0].equals("1"))
            exists = true;
        return exists;
    }
}
