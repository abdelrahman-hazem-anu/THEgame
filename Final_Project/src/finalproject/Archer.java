package finalproject;

public class Archer extends Fighter {
    
    public Archer(String name, int health, double x, double y, double speed, long lastShootTime, double length, double width, long specialCooldownTime, long lastSpecialUseTime) {
        super(name, health, x, y, speed, lastShootTime, length, width, WeaponFactory.createArcherWeapon(), specialCooldownTime, lastSpecialUseTime);
        
        this.addWeapon(WeaponFactory.createPistol());
    }

    @Override
    public boolean useSpecialAbility() {
        if (canUseSpecial()) {
            setSpeed(getBaseSpeed() * 1.5); 
            
            setLastSpecialUseTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }
}