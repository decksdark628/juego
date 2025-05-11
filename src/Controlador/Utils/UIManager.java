package Controlador.Utils;

import Controlador.Entities.Player;

import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;

public class UIManager {

    private Player player;
    private Text HPText;
    private Text scoreText;
    private Text timerText;
    private Text levelText; 
    private ProgressBar levelProgressBar; 
    private ProgressBar ammunitionBar;
    private Rectangle healthBar;
    private Rectangle healthBarBackground;
    private StackPane uiContainer;
    private VBox healthBarContainer;
    private Text ammunitionText;
    private Text skillPointText;


    private static final double HEALTH_BAR_WIDTH = 60;
    private static final double HEALTH_BAR_HEIGHT = 8;

    public UIManager(Player player,
                     Text HPText, Text scoreText, Text timerText,
                     Rectangle healthBar, Rectangle healthBarBackground,
                     VBox healthBarContainer, StackPane uiContainer,
                     Text levelText, ProgressBar levelProgressBar,
                     Text ammunitionText, ProgressBar ammunitionBar, Text skillPointText) {
        this.player = player;
        this.HPText = HPText;
        this.scoreText = scoreText;
        this.timerText = timerText;
        this.healthBar = healthBar;
        this.healthBarBackground = healthBarBackground;
        this.healthBarContainer = healthBarContainer;
        this.uiContainer = uiContainer;
        this.levelText = levelText;
        this.levelProgressBar = levelProgressBar;
        this.ammunitionBar = ammunitionBar;
        this.ammunitionText = ammunitionText;
        this.skillPointText= skillPointText;
        initializeHealthBar();
    }

    private void initializeHealthBar() {
        healthBarContainer.setVisible(true);
        healthBarBackground.setWidth(HEALTH_BAR_WIDTH);
        healthBar.setWidth(HEALTH_BAR_WIDTH);
        healthBar.setHeight(HEALTH_BAR_HEIGHT);
        healthBarBackground.setHeight(HEALTH_BAR_HEIGHT);
    }

    public void updateUI(int elapsedSeconds) {
        Platform.runLater(() -> {
            updateText(elapsedSeconds);
            updateHealthUI();
            updateLevelUI();
            bringUIToFront();
            updateAmmunitionBar();
        });
    }

    private void updateLevelUI() {
        levelText.setText("Nivel " + player.getLevel());
        levelProgressBar.setProgress(player.getProgressToNextLevel());
    }

    private void updateHealthUI() {
        double healthPercentage = (double) player.getHp() / player.getMaxHp();

        updateHealthBar();
        updateHealthText();

        if (healthPercentage <= 0.35) {
            healthBar.setFill(Color.RED);
            HPText.setFill(Color.RED);
        } else {
            healthBar.setFill(Color.LIMEGREEN);
            HPText.setFill(Color.LIMEGREEN);
        }
    }
    private void updateHealthText() {
        HPText.setText(player.getHp() + "/" + player.getMaxHp());
    }

    private void updateHealthBar() {
        double healthPercentage = (double) player.getHp() / player.getMaxHp();
        healthBar.setWidth(healthBarBackground.getWidth() * healthPercentage);
    }

    private void updateText(int elapsedSeconds){
        updateScoreText();
        updateTimerText( elapsedSeconds);
        updateAmmunitionText();
        updateSkillPointText();
    }

    private void updateScoreText() {
        scoreText.setText("Puntuación: " + player.getScore());
    }

    private void updateTimerText(int elapsedSeconds) {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateSkillPointText() {
        skillPointText.setText("Puntos de habilidad: " + player.getSkillPoints());
    }

    public void updateAmmunitionText() {
        if (player.isReloading()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - player.getLastReloadTime();
            double reloadProgress = Math.min((double) elapsedTime / player.getReloadTime(), 1.0); 
            int progressPercentage = (int) (reloadProgress * 100); 
            ammunitionText.setText("Recargando.." + progressPercentage + "%");
        } else {
            ammunitionText.setText("Municion: " + player.getCurrentAmmunition() + "/" + player.getMaxAmmunition());
        }
    }

    public void updateAmmunitionBar() {
        if (player.isReloading()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - player.getLastReloadTime();
            double reloadProgress = Math.min((double) elapsedTime / player.getReloadTime(), 1.0); 
            ammunitionBar.setProgress(reloadProgress);
        } else {
            ammunitionBar.setProgress(player.getAmmunitionBarPorcentage());
        }
    }

    private void bringUIToFront() {
        uiContainer.toFront();
        healthBarContainer.toFront();
        HPText.toFront();
        levelText.toFront();
        levelProgressBar.toFront();
    }
}