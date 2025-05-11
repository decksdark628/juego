package Controlador.Utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Random;
import Modelo.SoundPaths;

public class BackgroundMusic {
    private MediaPlayer mediaPlayer;

    private static final String[] GAME_TRACKS = {
            SoundPaths.GAME_TRACK_1,
            SoundPaths.GAME_TRACK_2,
            SoundPaths.GAME_TRACK_3,
            SoundPaths.GAME_TRACK_4};

    private static final String[] MENU_TRACKS = {
            SoundPaths.MENU_TRACK_1,
            SoundPaths.MENU_TRACK_2};

    private static BackgroundMusic sharedMenuMusic = null; 

    private static final Random RANDOM = new Random();
    private int lastTrackIndex = -1;
    private boolean isMenuMusic = false;
    private String currentTrack;

    public BackgroundMusic() {
        playNextRandomTrack();
    }

    public BackgroundMusic(String resourcePath) {
        Media backgroundMusic = new Media(getClass().getResource(resourcePath).toString());
        mediaPlayer = new MediaPlayer(backgroundMusic);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        //mediaPlayer.setVolume(0.1);
    }

    public static String getRandomGameTrack() {
        return GAME_TRACKS[RANDOM.nextInt(GAME_TRACKS.length)];
    }

    public static String getRandomMenuTrack() {
        return MENU_TRACKS[RANDOM.nextInt(MENU_TRACKS.length)];
    }

    public static BackgroundMusic getMenuMusic() {
        if (sharedMenuMusic == null) {
            sharedMenuMusic = new BackgroundMusic(getRandomMenuTrack());
            sharedMenuMusic.setVolume(0.3);
            sharedMenuMusic.isMenuMusic = true;
            sharedMenuMusic.mediaPlayer.setCycleCount(1);
            sharedMenuMusic.mediaPlayer.setOnEndOfMedia(sharedMenuMusic::playNextRandomMenuTrack);
        }
        return sharedMenuMusic;
    }

    private void playNextRandomTrack() {
        int index;
        do {
            index = RANDOM.nextInt(GAME_TRACKS.length);
        } while (index == lastTrackIndex);
        lastTrackIndex = index;
        playTrack(GAME_TRACKS[index]);
    }

    private void playNextRandomMenuTrack() {
        String next;
        do {
            next = getRandomMenuTrack();
        } while (next.equals(currentTrack));
        playTrack(next);
    }

    private void playTrack(String resourcePath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        Media media = new Media(getClass().getResource(resourcePath).toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.3);
        mediaPlayer.setCycleCount(1);

        currentTrack = resourcePath;

        if (isMenuMusic) {
            mediaPlayer.setOnEndOfMedia(this::playNextRandomMenuTrack);
        } else {
            mediaPlayer.setOnEndOfMedia(this::playNextRandomTrack);
        }
        mediaPlayer.play();
    }

    public void play() {
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }
    
    public void stop() {
        mediaPlayer.stop();
    }

    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume); 
    }
}
