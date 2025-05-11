package Modelo;

public class GameConstants {
    public static final double N=2;

    public static final double MAP_WIDTH = 3072;
    public static final double MAP_HEIGHT = 2304;
    public static final double TILE_SIZE = 512;
    
    public static final double PLAYER_SIZE = 70;
   
    public static final double MIN_SPAWN_DISTANCE = 300;
    public static final double MAX_SPAWN_DISTANCE = 800;
    public static final int DAMAGE_INTERVAL = 1500;
    
    public static final double HITBOX_SCALE = 0.65;
    
    public static final double PLAYER_PROJECTILE_SPEED = (6*N);
    public static final double ENEMY_PROJECTILE_SPEED = (3*N);
    public static final long REGENERATION_INTERVAL = 3000;

    public static final int ENEMY1_FIRST_SPAWN_WAVE = 1;
    public static final int ENEMY2_FIRST_SPAWN_WAVE = 5;
    public static final int ENEMY3_FIRST_SPAWN_WAVE = 10;

    public static final int ENEMY1_WAVE_INTERVAL = 1;
    public static final int ENEMY2_WAVE_INTERVAL = 3;
    public static final int ENEMY3_WAVE_INTERVAL = 3;

}