package Controlador.Utils;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.Random;
import Modelo.SoundPaths;

public class SoundManager {
    private MediaPlayer playerShooting;
    private MediaPlayer playerReloading;
    private AudioClip playerLvlUp;
    private AudioClip upgradeSuccess;
    private AudioClip upgradeError;
    private AudioClip buttonClick;
    private AudioClip playerHurt1;
    private AudioClip playerHurt2;
    private AudioClip playerDeath;
    private MediaPlayer enemyDeath;
    private AudioClip explosion;

    private static final double DEFAULT_VOLUME = 0.7;
    private static final Random RANDOM = new Random();
    private double reloadClipDurationMs = 2000; 

    public SoundManager() {
        Media shootMedia = new Media(getClass().getResource(SoundPaths.SHOOT).toString());
        playerShooting = new MediaPlayer(shootMedia);
        playerShooting.setCycleCount(1);
        playerShooting.setStartTime(Duration.seconds(0.15));
        playerShooting.setVolume(0.2);

        Media reloadMedia = new Media(getClass().getResource(SoundPaths.RELOAD).toString());
        playerReloading = new MediaPlayer(reloadMedia);
        playerReloading.setCycleCount(1);
        playerReloading.setOnReady(() -> reloadClipDurationMs = playerReloading.getMedia().getDuration().toMillis());

        playerLvlUp = loadClip(SoundPaths.PLAYER_LVL_UP);
        upgradeSuccess = loadClip(SoundPaths.UPGRADE_SUCCESS);
        upgradeError   = loadClip(SoundPaths.UPGRADE_ERROR);
        buttonClick    = loadClip(SoundPaths.BUTTON_CLICK);
        playerHurt1    = loadClip(SoundPaths.PLAYER_HURT_1);
        playerHurt2    = loadClip(SoundPaths.PLAYER_HURT_2);
        playerDeath    = loadClip(SoundPaths.PLAYER_DEATH);

        Media enemyMedia = new Media(getClass().getResource(SoundPaths.ENEMY_DEATH).toString());
        enemyDeath = new MediaPlayer(enemyMedia);
        enemyDeath.setCycleCount(1);
        enemyDeath.setStartTime(Duration.seconds(0.3));
        enemyDeath.setVolume(0.7);

        explosion      = loadClip(SoundPaths.EXPLOSION);

        playerLvlUp.setCycleCount(1);
    }

    private AudioClip loadClip(String path) {
        AudioClip clip = new AudioClip(getClass().getResource(path).toString());
        clip.setVolume(DEFAULT_VOLUME);
        return clip;
    }

    public boolean isPlayerShootingPlaying() {
        return playerShooting.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public boolean isPlayerReloadingPlaying() {
        return playerReloading.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void playPlayerShooting() {
        if (playerShooting.getStatus() == MediaPlayer.Status.PLAYING) {
            playerShooting.stop();
        }
        playerShooting.seek(Duration.seconds(0.15));
        playerShooting.play();
    }

    public void playPlayerReloading(double reloadTimeMs) {
        if (playerReloading.getStatus() == MediaPlayer.Status.PLAYING) {
            playerReloading.stop();
        }
        double playbackRate = reloadClipDurationMs / reloadTimeMs;
        if (playbackRate <= 0) playbackRate = 1.0;

        playerReloading.setRate(playbackRate);
        playerReloading.seek(Duration.ZERO);
        playerReloading.play();
    }
    
    public void playPlayerLvlUp(){
        playerLvlUp.setCycleCount(1);
        playerLvlUp.play();
    }

    public void playUpgradeSuccess() {
        upgradeSuccess.play();
    }

    public void playUpgradeError() {
        upgradeError.setVolume(1);
        upgradeError.play();
    }

    public void playButton() {
        buttonClick.setVolume(1);
        buttonClick.play();
    }

    public void playPlayerDamage() {
        if (RANDOM.nextBoolean()) {
            playerHurt1.play();
            playerHurt1.setVolume(1);
        } else {
            playerHurt2.play();
            playerHurt2.setVolume(1);
        }
    }

    public void playPlayerDeath() {
        playerDeath.play();
        playerDeath.setVolume(0.5);
    }

    public void playEnemyDeath() {
        if (enemyDeath.getStatus() == MediaPlayer.Status.PLAYING) {
            enemyDeath.stop();
        }
        enemyDeath.seek(Duration.seconds(0.3));
        enemyDeath.play();
    }

    public void playExplosion() {
        explosion.setVolume(1);
        explosion.play();
    }
}
