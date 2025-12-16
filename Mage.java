package finalproject;

public class Mage extends Fighter{
    
    public Mage(String name, int health, double x, double y, double speed, long lastShootTime, double length, double width, long specialCooldownTime, long lastSpecialUseTime) {
        super(name, health, x, y, speed, lastShootTime, length, width, WeaponFactory.createMageWeapon(), specialCooldownTime, lastSpecialUseTime);
        
        // Add a secondary weapon (e.g., Pistol) to enable weapon switching functionality
        this.addWeapon(WeaponFactory.createPistol());
    }

    @Override
    public boolean useSpecialAbility() {
        if (canUseSpecial()) {
            Weapons currentWeapon = getWeapon();
            int originalDamage = getBaseWeapon().getDamage(); // Use base weapon damage for the buff calculation
            
            // Create a temporary, buffed weapon object
            Weapons buffedWeapon = new Weapons(
                currentWeapon.getName() + " (Buffed)",
                currentWeapon.getProjectileSpeed(),
                originalDamage * 2, // Double damage
                currentWeapon.getCooldownTime() 
            );
            
            // Set the new weapon. Fighter.checkSpecialEffectExpiration will switch it back to baseWeapon.
            setWeapon(buffedWeapon); 
            
            setLastSpecialUseTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }
}