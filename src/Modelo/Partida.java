package Modelo;

public class Partida {
    private int id;
    private String timestamp;
    private int score;
    private Usuario user;
    
    public Partida(int id, String timestamp, int score, Usuario user){
        this.id = id;
        this.timestamp = timestamp;
        this.score = score;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Partida{" + "id=" + id + ", timestamp=" + timestamp + ", score=" + score + ", user=" + user + '}';
    }
    
}
