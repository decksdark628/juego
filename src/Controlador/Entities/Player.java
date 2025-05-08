package Controlador.Entities;

import Modelo.GameConstants;

public class Player extends GameEntity {
    private int maxHp;
    private int hp;
    private int damage;
    private int score;
    private int hpRegen;
    private boolean wPressed, aPressed, sPressed, dPressed;
    private double angle;
    private int level;
    private int skillPoints;
    private int scoreToNextLevel;
    private long lastRegenTime;
    private int auxScore;
    private int maxAmmunition;
    private int currentAmmunition;
    private long reloadTime; 
    private long lastReloadTime;


    public Player(double x, double y) {
        super(x, y, 
              GameConstants.PLAYER_SIZE, 
              GameConstants.PLAYER_SIZE, 
              3);
        this.maxHp = 50;
        this.hp = maxHp;
        this.damage = 1;
        this.hpRegen = 1;
        this.score = 0;
        this.level = 1;
        this.skillPoints = 0;
        this.scoreToNextLevel = 100; 
        this.lastRegenTime = System.currentTimeMillis();
        this.auxScore = 0;
        this.maxAmmunition = 10; 
        this.currentAmmunition = maxAmmunition; 
        this.reloadTime = 2950; 
        this.lastReloadTime = 0; 
    }

    public void move() {
        double dx = calculateHorizontalMovement();
        double dy = calculateVerticalMovement();

        if (isDiagonalMovement(dx, dy)) {
            double factor = normalizeDiagonalMovement(dx, dy);
            dx *= factor;
            dy *= factor;
        }

        double newX = x + dx;
        double newY = y + dy;

        if (newX < 0) {
            dx = -x; 
        } else if (newX + width > GameConstants.MAP_WIDTH) {
            dx = GameConstants.MAP_WIDTH - (x + width); 
        }

        if (newY < 0) {
            dy = -y; 
        } else if (newY + height > GameConstants.MAP_HEIGHT) {
            dy = GameConstants.MAP_HEIGHT - (y + height); 
        }

        updatePosition(dx, dy);
    }

    private double calculateHorizontalMovement() {
        double dx = 0;
        if (aPressed) dx -= speed;
        if (dPressed) dx += speed;
        return dx;
    }

    private double calculateVerticalMovement() {
        double dy = 0;
        if (wPressed) dy -= speed;
        if (sPressed) dy += speed;
        return dy;
    }

    private boolean isDiagonalMovement(double dx, double dy) {
        return dx != 0 && dy != 0;
    }

    private double normalizeDiagonalMovement(double dx, double dy) {
        return speed / Math.sqrt(dx * dx + dy * dy);
    }

    public boolean Canshoot() {
        if (isReloading()) {
            return false; 
        }

        if (currentAmmunition > 0) {
            currentAmmunition--;
            return true;
        } else {
            reload();
            return false;
        }
    }
    
    /*public void reload() {
        if (canReload()) {
            long currentTime = System.currentTimeMillis();
            double elapsedTime = currentTime - lastReloadTime;
            double gap = maxAmmunition - currentAmmunition;
            double timeXBullet = reloadTime / gap;
        
            while (gap>0 && elapsedTime >= timeXBullet) {
                currentAmmunition++;
                gap--;
                elapsedTime -= timeXBullet;
            }          
            if (gap == 0) {
                lastReloadTime = currentTime; 
            } else {
                lastReloadTime += timeXBullet * gap; 
            }
        }
    }*/

    public void reload() {
        if(canReload()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastReloadTime >= reloadTime) {
                currentAmmunition = maxAmmunition; 
                lastReloadTime = currentTime; 
            }
        }
    }

    public boolean canReload() {
        return currentAmmunition < maxAmmunition && !isReloading();
    }

    public boolean isReloading() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastReloadTime) < reloadTime;
    }

    public void hit(double damage) {
        reduceHealth(damage);
        ensureHealthIsNonNegative();
        lastRegenTime = System.currentTimeMillis(); 
    }

    private void reduceHealth(double damage) {
        hp -= damage;
    }

    private void ensureHealthIsNonNegative() {
        if (hp < 0) hp = 0;
    }

    public void regenerateHp() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRegenTime >= GameConstants.REGENERATION_INTERVAL && canRegenerateHealth()) {
            increaseHealth(getHpRegen());
            ensureHealthDoesNotExceedMax();
            lastRegenTime = currentTime; 
        }
    }

    private boolean canRegenerateHealth() {
        return hp < maxHp;
    }

    private void ensureHealthDoesNotExceedMax() {
        if (hp > maxHp) hp = maxHp;
    }

    public void addScore(int points) {
        score += points;
  
    }
    
    public void addAuxScore(int points){
        auxScore +=points;
        checkLevelUp();
    }
    
    private void checkLevelUp() {
        while (auxScore >= scoreToNextLevel) {
            auxScore -= scoreToNextLevel; 
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        skillPoints++;
        scoreToNextLevel *= 1.65; 
        hp = Math.min(hp + maxHp / 10, maxHp);
    }

    public void setMovementKeyPressed(String key, boolean pressed) {
        switch (key.toUpperCase()) {
            case "W":
                wPressed = pressed;
                break;
            case "A":
                aPressed = pressed;
                break;
            case "S":
                sPressed = pressed;
                break;
            case "D":
                dPressed = pressed;
                break;
        }
    }
    
    public boolean hasSkillPointsToSpend(){ 
       return skillPoints > 0;
    }
    
   public void increaseSpeed(double s){
       this.speed += s;
       skillPoints--;
   }
    public void increaseHealth(int hp) {
        this.hp += hp;
    }
   public void upgradeMaxHp(int hp){
       this.maxHp += hp;
       skillPoints--;
   }
   public void upgradeDamage(int d){
       this.damage +=d;
       skillPoints--;
   }
   public void upgradeHpRegen(int r){
       this.hpRegen +=r;
       skillPoints--;
   }
    public void upgradeMaxAmmunition(int a){
         this.maxAmmunition += a;
         skillPoints--;
         currentAmmunition = maxAmmunition;
    }
    public void upgradeReloadTime(long r){
        if(reloadTime - r >= 0){
            this.reloadTime -= r;
            skillPoints--;
        } else {
            this.reloadTime = 250;
        }
    }
    
    public void setAngle(double angle) { this.angle = angle; }
    public double getSpeed() { return speed; }
    public int getHp() { return hp; }
    public int getHpRegen() { return hpRegen; }
    public int getDamage() { return damage; }
    public int getMaxHp() { return maxHp; }
    public int getScore() { return score; }
    public boolean isDead() { return hp <= 0; }
    public double getAngle() { return angle; }
    public int getLevel() { return level; }
    public int getSkillPoints() { return skillPoints; }
    public int getScoreToNextLevel() { return scoreToNextLevel; }
    public double getProgressToNextLevel() { return Math.min((double) auxScore / scoreToNextLevel, 1.0); }
    public int getMaxAmmunition() { return maxAmmunition;}
    public int getCurrentAmmunition() { return currentAmmunition;} 
    public long getReloadTime() { return reloadTime; }
    public long getLastReloadTime() { return lastReloadTime; }   
    public double getAmmunitionBarPorcentage() { return Math.min((double) currentAmmunition / maxAmmunition, 1.0); }
}