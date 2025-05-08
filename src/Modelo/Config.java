package Modelo;

public abstract class Config {
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    
    public static final int START_SCREEN = 0;
    
    public static final String GAME_TITLE = "Proyecto Intermodular";
    public static final String DATABASE_NAME = "juego";
    public static final String[] DATABASE_TABLES = {"usuarios", "partidas", "comentarios"};
    public static final String CREATE_DB_TABLES = "./src/resources/scripts/init.sql";
    public static final String SAMPLE_DATA_PATH = "./src/Resources/scripts/demo-data.sql";
    public static final boolean ASK_TO_IMPORT_SAMPLE_DATA = true; //// cambiar a false luego de la demostracion
    
    public static String getStartScenePath(){
        String path = "";
        switch (START_SCREEN){
            case 0:
                path = ScenePaths.LOGIN_SCREEN;
                break;
            case 1:
                path = ScenePaths.GAME_SCREEN;
                break;
            case 2:
                path = ScenePaths.GAME_OVER;
                break;
            case 3:
                path = ScenePaths.SCORES;
                break;
            case 4:
                path = ScenePaths.UPGRADE_MENU;
                break;
            case 5:
                path = ScenePaths.PAUSE_MENU;
                break;
        }
        return path;
    }
}
