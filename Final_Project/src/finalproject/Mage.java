package finalproject;

public class Mage extends Fighter{
    
    public Mage(String name, int health, double x, double y, double speed, long lastShootTime, double length, double width, long specialCooldownTime, long lastSpecialUseTime) {
        super(name, health, x, y, speed, lastShootTime, length, width, WeaponFactory.createMageWeapon(), specialCooldownTime, lastSpecialUseTime);
        
        this.addWeapon(WeaponFactory.createPistol());
    }

    @Override
    public boolean useSpecialAbility() {
        if (canUseSpecial()) {
            Weapons currentWeapon = getWeapon();
            int originalDamage = getBaseWeapon().getDamage(); 
            
            Weapons buffedWeapon = new Weapons(
                currentWeapon.getName() + " (Buffed)",
                currentWeapon.getProjectileSpeed(),
                originalDamage * 2, 
                currentWeapon.getCooldownTime() 
            );
            
            setWeapon(buffedWeapon); 
            
            setLastSpecialUseTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }
}