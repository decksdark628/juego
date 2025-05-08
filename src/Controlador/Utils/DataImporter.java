
package Controlador.Utils;

import Modelo.Comentario;
import Modelo.Partida;
import Modelo.Usuario;
import java.util.HashMap;
import static utilidades.bbdd.Bd.consultaSelect;
import static Controlador.Utils.Database.dbConnection;
import java.util.ArrayList;

public class DataImporter {
    private static ArrayList<Usuario> users = new ArrayList<>();
    private static ArrayList<Partida> partidas = new ArrayList<>();
    private static ArrayList<Comentario> comentarios = new ArrayList<>();
    private static HashMap<String, Usuario> usersByTheirAlias;
    private static HashMap<String, Partida> partidasByTheirId;
    
    public static void init(){
        loadUsuarios();
        loadPartidas();
        loadComentarios();
    }
    
    public static void createUsersHashMap(){
        usersByTheirAlias = new HashMap<>();
        for (Usuario user: users){
            usersByTheirAlias.putIfAbsent(user.getAlias(), user);
        }
    }
    
    public static void createPartidasHashMap(){
        partidasByTheirId = new HashMap<>();
        for (Partida partida: partidas){
            partidasByTheirId.putIfAbsent(partida.getId()+"", partida);
        }
    }
    
    public static void loadUsuarios(){
        String sqlQuery = "select alias, nivel from usuarios";
        String[][] temp = consultaSelect(dbConnection, sqlQuery);
        int startingPoint = users.size();
        
        for (int i = startingPoint; i < temp.length; i++){
            users.add(new Usuario(temp[i][0], Integer.parseInt(temp[i][1])));
        }
    }
    
    public static void loadPartidas(){
        String sqlQuery = "select id, fecha_hora, puntaje, alias_usuario from partidas";
        String[][] temp = consultaSelect(dbConnection, sqlQuery);
        int startingPoint = partidas.size();
        createUsersHashMap();
        
        
        for (int i = startingPoint; i < temp.length; i++){
            partidas.add(new Partida(Integer.parseInt(temp[i][0]), temp[i][1], Integer.parseInt(temp[i][2]), usersByTheirAlias.get(temp[i][3])));
        }
    }
    
    public static void loadComentarios(){
        String sqlQuery = "select id, texto, id_partida, alias_usuario from comentarios";
        String[][] temp = consultaSelect(dbConnection, sqlQuery);
        int startingPoint = comentarios.size();
        createPartidasHashMap();
        
        for (int i = startingPoint; i < temp.length; i++){
            comentarios.add(new Comentario(Integer.parseInt(temp[i][0]), temp[i][1], partidasByTheirId.get(temp[i][2]), usersByTheirAlias.get(temp[i][3])));
        }
    }

    public static ArrayList<Usuario> getUsers() {
        return users;
    }

    public static void setUsers(ArrayList<Usuario> users) {
        DataImporter.users = users;
    }

    public static ArrayList<Partida> getPartidas() {
        return partidas;
    }

    public static void setPartidas(ArrayList<Partida> partidas) {
        DataImporter.partidas = partidas;
    }

    public static ArrayList<Comentario> getComentarios() {
        return comentarios;
    }

    public static void setComentarios(ArrayList<Comentario> comentarios) {
        DataImporter.comentarios = comentarios;
    }

    public static HashMap<String, Usuario> getUsersByTheirAlias() {
        return usersByTheirAlias;
    }

    public static void setUsersByTheirAlias(HashMap<String, Usuario> usersByTheirAlias) {
        DataImporter.usersByTheirAlias = usersByTheirAlias;
    }

    public static HashMap<String, Partida> getPartidasByTheirId() {
        return partidasByTheirId;
    }

    public static void setPartidasByTheirId(HashMap<String, Partida> partidasByTheirId) {
        DataImporter.partidasByTheirId = partidasByTheirId;
    }
    
}
