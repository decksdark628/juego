package Modelo;

public class Usuario {
    private String alias;
    private int level;
    
    public Usuario(String alias, int level){
        this.alias = alias;
        this.level = level;
    }
    
    public String translateLevelToText(){
        String temp = "-";
        
        switch(level){
            case 0:
                temp = "Principiante";
                break;
            case 1:
                temp = "Avanzado";
                break;
            case 2:
                temp = "Experto";
        }
        
        return temp;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Usuario{" + "alias=" + alias + ", level=" + level + '}';
    }
    
}
