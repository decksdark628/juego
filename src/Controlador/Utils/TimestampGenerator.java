package Controlador.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class TimestampGenerator {
    public static String generate(){
        LocalDateTime dateAndTime = LocalDateTime.now();
        DateTimeFormatter longFormTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String currentTimestamp = dateAndTime.format(longFormTimestamp);
        
        return currentTimestamp;
    }
}
