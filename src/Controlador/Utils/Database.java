package Controlador.Utils;

import Modelo.Config;
import Modelo.PlayerSessionData;
import static utilidades.bbdd.Bd.consultaModificacion;
import utilidades.bbdd.Gestor_conexion_POSTGRE;
import static utilidades.bbdd.Bd.consultaSelect;
import static utilidades.bbdd.Bd.importar;

public class Database {
    public static Gestor_conexion_POSTGRE dbConnection;
    private static boolean tablesFound;
       
    public static void init() {
        dbConnection = new Gestor_conexion_POSTGRE(Config.DATABASE_NAME, true);
        try {
            tablesFound = allTablesExist(Config.DATABASE_TABLES);
        }
        catch (Error e){
            tablesFound = false;
        }  
    }
    
    public static boolean allTablesExist(String[] tables){
        boolean allExist = true;
        
        for(int i = 0; i<tables.length && allExist; i++){
            if (!(consultaSelect(dbConnection, "select exists(select 1 from pg_tables where tablename = '" + tables[i] + "')")[0][0].equals("t")))
                allExist = false;
        }
        
        return allExist;
    }
    
    public static boolean insertSampleData(){
        try {
            importar(Config.SAMPLE_DATA_PATH,dbConnection);
            return true;
        }
        catch (Error e){
            return false;
        }
    }
    
    public static void sendPlayerScore(){
        consultaModificacion(Database.getDbConnection(), "insert into partidas (puntaje, fecha_hora, alias_usuario) values ('" + PlayerSessionData.getLocalScore() + "', '" + PlayerSessionData.getDeathTimestamp() + "', '" + PlayerSessionData.getLocalUsername() + "')");
    }

    // getters and setters //
    public static Gestor_conexion_POSTGRE getDbConnection(){
        return dbConnection;
    }

    public static boolean getTablesFound() {
        return tablesFound;
    }

    public static void setTablesFound(boolean tablesFound) {
        Database.tablesFound = tablesFound;
    }
    
}
