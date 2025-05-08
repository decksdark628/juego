package Modelo;

public class Comentario {
    private int id;
    private String text;
    private Partida partida;
    private Usuario user;
    
    public Comentario(int id, String text, Partida partida, Usuario user){
        this.id = id;
        this.text = text;
        this.partida = partida;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comentario{" + "id=" + id + ", text=" + text + ", partida=" + partida + ", user=" + user + '}';
    }
    
}

