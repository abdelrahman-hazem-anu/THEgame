package finalproject;

public class Warrior extends Fighter {
    
    public Warrior(String name, int health, double x, double y, double speed, long lastShootTime, double length, double width, long specialCooldownTime, long lastSpecialUseTime) {
        super(name, health, x, y, speed, lastShootTime, length, width, WeaponFactory.createWarriorWeapon(), specialCooldownTime, lastSpecialUseTime);
        
        this.addWeapon(WeaponFactory.createCannon());
    }

    @Override
    public boolean useSpecialAbility() {
        if (canUseSpecial()) {
            int currentHealth = getHealth();
            setHealth(currentHealth + 30); 
            
            setLastSpecialUseTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }
}