package finalproject;

import java.util.ArrayList;
import java.util.List;

public abstract class Fighter {
    private String name;
    private int health;
    private double x;
    private double y;
    private double speed;
    private final double baseSpeed; 
    private Weapons baseWeapon; 
    private long lastShootTime;
    private double length;
    private double width;
    private Weapons weapon;
    private final long specialCooldownTime;
    private long lastSpecialUseTime;
    
    private static final long SPECIAL_EFFECT_DURATION_MS = 5000; 
    
    private List<Weapons> inventory;
    private int currentWeaponIndex = 0; 
    private int facingDirection; 

    public Fighter(String name, int health, double x, double y, double speed, long lastShootTime, double length, double width, Weapons weapon, long specialCooldownTime, long lastSpecialUseTime) {
        this.name = name;
        this.health = health;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.baseSpeed = speed; 
        this.lastShootTime = lastShootTime;
        this.length = length;
        this.width = width;
        this.weapon = weapon;
        this.baseWeapon = weapon; 
        this.specialCooldownTime = specialCooldownTime;
        this.lastSpecialUseTime = lastSpecialUseTime;

        this.inventory = new ArrayList<>();
        this.inventory.add(weapon); 
        this.facingDirection = 1; 
    }
    
    public void addWeapon(Weapons w) {
        if (w != null && !this.inventory.contains(w)) {
            this.inventory.add(w);
        }
    }
    
    public boolean switchWeapon(int index) {
        if (index >= 0 && index < inventory.size()) {
            this.currentWeaponIndex = index;
            this.weapon = inventory.get(index);
            return true;
        }
        return false;
    }
 
    public void moveUp(double boundary) {
        y -= speed;
        if (y < boundary) y = boundary;
    }
    
    public void moveDown(double boundary) {
        y += speed;
        if (y > boundary - length) y = boundary - length;
    }
    
    public void moveLeft(double boundary) {
        x -= speed;
        if (x < boundary) x = boundary;
        setFacingDirection(-1); 
    }
    
    public void moveRight(double boundary) {
        x += speed;
        if (x > boundary - width) x = boundary - width;
        setFacingDirection(1); 
    }
    
    public void checkSpecialEffectExpiration() {
        long timeSinceSpecial = System.currentTimeMillis() - lastSpecialUseTime;
        if (timeSinceSpecial >= SPECIAL_EFFECT_DURATION_MS) {
            if (speed != baseSpeed) speed = baseSpeed;
            if (weapon != baseWeapon && weapon.getName().endsWith("(Buffed)")) {
                setWeapon(baseWeapon);
            }
        }
    }

    public boolean canShoot() {
        if (weapon == null) return false;
        long now = System.currentTimeMillis();
        return (now - lastShootTime) >= weapon.getCooldownTime();
    }

    public Projectile shoot() {
        if (!canShoot() || weapon == null) return null;

        double dirX = facingDirection; 
        double dirY = 0.0;             

        double spawnX = (dirX > 0) ? (x + width) : (x - 10);
        double spawnY = y + length / 2.0;

        Projectile p = new Projectile (
                spawnX,
                spawnY,
                dirX,
                dirY,
                weapon.getProjectileSpeed(),
                weapon.getDamage()
        );
        lastShootTime = System.currentTimeMillis();
        return p;
    }

    public void takeDamage(int dmg) {
        if (dmg <= 0) return;
        setHealth(this.health - dmg);
    }

    public boolean isDead() {
        return this.health <= 0;
    }

    public boolean collidesWith(Projectile p) {
        double px = p.getX();
        double py = p.getY();
        return px >= x && px <= x + width && py >= y && py <= y + length;
    }
    
    public boolean canUseSpecial() {
        return (System.currentTimeMillis() - lastSpecialUseTime) >= specialCooldownTime;
    }

    public abstract boolean useSpecialAbility();

    public int getFacingDirection() { return facingDirection; }
    public void setFacingDirection(int direction) { if (direction == 1 || direction == -1) this.facingDirection = direction; }
    public String getName() { return name; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public double getBaseSpeed() { return baseSpeed; }
    public double getLength() { return length; }
    public double getWidth() { return width; }
    public Weapons getWeapon() { return weapon; }
    public Weapons getBaseWeapon() { return baseWeapon; }
    public void setWeapon(Weapons weapon) { 
        this.weapon = weapon; 
        if (weapon == baseWeapon) this.currentWeaponIndex = inventory.indexOf(baseWeapon);
    }
    public long getLastSpecialUseTime() { return lastSpecialUseTime; }
    public void setLastSpecialUseTime(long lastSpecialUseTime) { this.lastSpecialUseTime = lastSpecialUseTime; }
    public long getSpecialCooldownTime() { return specialCooldownTime; }
    public List<Weapons> getInventory() { return inventory; }
    public int getCurrentWeaponIndex() { return currentWeaponIndex; }
}