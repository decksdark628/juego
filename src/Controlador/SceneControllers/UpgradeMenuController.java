package Controlador.SceneControllers;

import Controlador.Entities.Player;
import Controlador.Cursor;
import Controlador.Utils.BackgroundMusic;
import Controlador.Utils.SoundManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;      
import javafx.scene.text.Text;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class UpgradeMenuController {

    @FXML private Button upgradeOption1; 
    @FXML private Button upgradeOption2;
    @FXML private Button upgradeOption3; 
    @FXML private Button upgradeOption4; 
    @FXML private Button upgradeOption5;
    @FXML private Button upgradeOption6; 
    @FXML private Button resumeButton;
    @FXML private Text availableSkillPointText;
    @FXML private Text reloadTimeTooltip;
    @FXML private Text ammoTooltip;
    @FXML private Text damageTooltip;
    @FXML private Text moveSpeedTooltip;
    @FXML private Text hpRegenTooltip;
    @FXML private Text hpTooltip;

    private Stage stage;
    private Scene gameScene; 
    private Player player;   
    private BackgroundMusic backgroundMusic;
    private BackgroundMusic menuMusic;

    private AnimationTimer gameLoop;
    private Timeline gameTimer;
    private Timeline enemySpawnerTimeline;
    private SoundManager soundManager;
    
    
    private int hp=10;
    private int hpRegen=1;
    private double speed=0.5;
    private int ammo=1;
    private int damage=5;
    private long reload=250;

    public void initialize() {
        this.soundManager = new SoundManager();
        setupUpgradeButtons();
        setupResumeButton();
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (player == null) {
            upgradeOption1.setDisable(true);
            upgradeOption2.setDisable(true);
            upgradeOption3.setDisable(true);
            upgradeOption4.setDisable(true);
            upgradeOption5.setDisable(true);
            upgradeOption6.setDisable(true);
        } else {
            upgradeOption1.setDisable(false);
            upgradeOption2.setDisable(false);
            upgradeOption3.setDisable(false);
            upgradeOption4.setDisable(false);
            upgradeOption5.setDisable(false);
            upgradeOption6.setDisable(false);
        }
        updateText();
    }
    

    public void setStage(Stage stage) {
        this.stage = stage;
        Platform.runLater(() -> {
            if (this.stage != null && this.stage.getScene() != null) {
                this.stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.SPACE) {
                        resumeGame();
                        event.consume();
                    }
                });
            }
        });
    }

    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    public void setGameTimers(AnimationTimer gameLoop, Timeline gameTimer, Timeline enemySpawnerTimeline) {
        this.gameLoop = gameLoop;
        this.gameTimer = gameTimer;
        this.enemySpawnerTimeline = enemySpawnerTimeline; 
    }

    public void setBackgroundMusic(BackgroundMusic backgroundMusic) {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
        this.backgroundMusic = backgroundMusic;

        menuMusic = BackgroundMusic.getMenuMusic();
        menuMusic.play();
    }

    private void setupUpgradeButtons() {
        upgradeOption1.setOnAction(event -> {
            if (player != null && player.getSkillPoints() > 0) {
                player.increaseSpeed(speed);
                soundManager.playUpgradeSuccess();
                updateText();
            } else {
                soundManager.playUpgradeError();
            }
        });

        upgradeOption2.setOnAction(event -> {
            if (player != null && player.getSkillPoints() > 0) {
                player.upgradeDamage(damage);
                soundManager.playUpgradeSuccess();
                updateText();
            } else {
                soundManager.playUpgradeError();
            }
        });

        upgradeOption3.setOnAction(event -> {
            if (player != null && player.getSkillPoints() > 0) {
                player.upgradeMaxHp(hp);
                player.increaseHealth(hp);
                soundManager.playUpgradeSuccess();
                updateText();
            } else {
                soundManager.playUpgradeError();
            }
        });
        upgradeOption4.setOnAction(event -> {
            if (player != null && player.getSkillPoints() > 0) {
                player.upgradeHpRegen(hpRegen);
                soundManager.playUpgradeSuccess();
                updateText();
            } else {
                soundManager.playUpgradeError();
            }
        });
    
        upgradeOption5.setOnAction(event -> {
            if (player != null && player.getSkillPoints() > 0) {
                player.upgradeMaxAmmunition(ammo);
                soundManager.playUpgradeSuccess();
                updateText();
            } else {
                soundManager.playUpgradeError();
            }
        });
    
        upgradeOption6.setOnAction(event -> {
            if (player != null && player.getSkillPoints() > 0) {
                player.upgradeReloadTime(reload); 
                soundManager.playUpgradeSuccess();
                updateText();
            } else {
                soundManager.playUpgradeError();
            }
        });
        
    }
    
    private void setupResumeButton() {
        resumeButton.setOnAction(event ->{
            soundManager.playButton();
            resumeGame();
        }); 
    }
   
    private void resumeGame() {
        if (stage != null && gameScene != null) {

            if (gameLoop != null) {
                gameLoop.start();
            } 
            if (gameTimer != null) {
                gameTimer.play();
            } 
            if (enemySpawnerTimeline != null) { 
                enemySpawnerTimeline.play();
            } 

            stage.setScene(gameScene);
            Platform.runLater(() -> {
                if (gameScene.getRoot() != null) {
                    gameScene.getRoot().requestFocus();
                } 
                 Cursor.applyCustomCursor(gameScene);
                 if (menuMusic != null) {
                     menuMusic.pause();
                 }
                 if (backgroundMusic != null) {
                     backgroundMusic.play();
                 }
            });
        }
    }
    
    private void updateAvailableSkillPointText(){
        availableSkillPointText.setText("Puntos Disponibles: "+player.getSkillPoints());
    }
    
    private void updateReloadTimeTooltip(){
        if(player.getReloadTime()  <= 250){
            reloadTimeTooltip.setText(player.getReloadTime()+"ms"+" --> "+player.getReloadTime()+"ms");
        } else {
            reloadTimeTooltip.setText(player.getReloadTime()+"ms"+" --> "+(player.getReloadTime()-reload)+"ms");
        }
    }
    
    private void updateAmmoToolTip(){
        ammoTooltip.setText(player.getMaxAmmunition()+" --> "+(player.getMaxAmmunition()+ammo));
    }
    
    private void updateDamageTooltip(){
        damageTooltip.setText(player.getDamage()+" --> "+(player.getDamage()+damage));
    }
    
    private void updateMoveSpeedTooltip(){
        moveSpeedTooltip.setText(player.getSpeed()+" --> "+(player.getSpeed()+speed));
    }
    
    private void updateHpRegenTooltip(){
        hpRegenTooltip.setText(player.getHpRegen()+" --> "+(player.getHpRegen()+hpRegen));
    }
            
    private void updateHpTooltip(){
        hpTooltip.setText(player.getMaxHp()+" --> "+(player.getMaxHp()+hp));
    }
    
    private void updateText(){
        updateAvailableSkillPointText();
        updateReloadTimeTooltip();
        updateAmmoToolTip();
        updateDamageTooltip();
        updateMoveSpeedTooltip();
        updateHpRegenTooltip();
        updateHpTooltip();
    }
}