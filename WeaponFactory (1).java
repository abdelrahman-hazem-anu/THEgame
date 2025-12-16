package finalproject;

public class WeaponFactory {
    
    public static Weapons createPistol() {
        return new Weapons("Pistol", 6.0, 15, 400);
    }

    public static Weapons createCannon() {
        return new Weapons("Cannon", 3.0, 30, 1000);
    }
    
    public static Weapons createWarriorWeapon() {
        return new Weapons("Axe/Sword", 4.5, 20, 600);
    }
    
    public static Weapons createArcherWeapon() {
        return new Weapons("Bow", 8.0, 12, 300);
    }
    
    public static Weapons createMageWeapon() {
        return new Weapons("Staff", 7.0, 25, 900);
    }
}