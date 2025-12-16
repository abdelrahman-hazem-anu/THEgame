package finalproject;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private static final double width = 800; 
    private static final double height = 800;
    
    private Fighter player1;
    private Fighter player2;
    private List<Projectile> projectiles;
    private boolean isGameOver = false;

    public Arena(Fighter player1, Fighter player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.projectiles = new ArrayList<>();
        initializePlayerPositions(); 
    }

    private void initializePlayerPositions() {
        double p1StartX = width * 0.1;
        double p1StartY = height / 2.0 - player1.getLength() / 2.0;
        player1.setX(p1StartX);
        player1.setY(p1StartY);
        
        double p2StartX = width * 0.9 - player2.getWidth();
        double p2StartY = height / 2.0 - player2.getLength() / 2.0;
        player2.setX(p2StartX);
        player2.setY(p2StartY);
        
        updateFighterFacing();
    }
    
    public void update() {
        if (isGameOver) return;
        
        player1.checkSpecialEffectExpiration();
        player2.checkSpecialEffectExpiration();
        
        updateFighterFacing();
        
        updateProjectiles();
        checkCollisions();
        checkGameOver();
    }

  
    private void updateFighterFacing() {
        if (player1.getX() < player2.getX()) {
            player1.setFacingDirection(1);  
            player2.setFacingDirection(-1); 
        } else {
            player1.setFacingDirection(-1);
            player2.setFacingDirection(1);  
        }
    }

    private void updateProjectiles() {
        for (Projectile p : projectiles) {
            p.updatePosition();
        }
        projectiles.removeIf(p -> p.getX() < -20 || p.getX() > width + 20 || p.getY() < -20 || p.getY() > height + 20);
    }

    private void checkCollisions() {
        List<Projectile> toRemove = new ArrayList<>();
        for (Projectile p : projectiles) {
            if (player2.collidesWith(p)) {
                player2.takeDamage(p.getDamage());
                toRemove.add(p);
                
                SoundManager.playSound("hit.wav"); 
            } 
            else if (player1.collidesWith(p)) {
                player1.takeDamage(p.getDamage());
                toRemove.add(p);
                
                SoundManager.playSound("hit.wav"); 
            }
        }
        projectiles.removeAll(toRemove);
    }

    private void checkGameOver() {
        if (player1.isDead() || player2.isDead()) {
            isGameOver = true;
        }
    }

    public void playerShoot(Fighter player) {
        Projectile p = player.shoot();
        if (p != null) {
            projectiles.add(p);
        }
    }

    public void enforceBounds(Fighter f) {
        f.setX(Math.max(0, Math.min(width - f.getWidth(), f.getX())));
        f.setY(Math.max(0, Math.min(height - f.getLength(), f.getY())));
    }
    
    public Fighter getPlayer1() { return player1; }
    public Fighter getPlayer2() { return player2; }
    public List<Projectile> getProjectiles() { return projectiles; }
    public boolean isGameOver() { return isGameOver; }
}