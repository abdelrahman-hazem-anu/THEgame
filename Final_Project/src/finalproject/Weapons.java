package finalproject;

public class Weapons {
    private String name;
    private double projectileSpeed;
    private int damage;
    private long cooldownTime;

    public Weapons(String name, double projectileSpeed, int damage, long cooldownTime) {
        this.name = name;
        this.projectileSpeed = projectileSpeed;
        this.damage = damage;
        this.cooldownTime = cooldownTime;
    }

    public String getName() {
        return name;
    }

    public double getProjectileSpeed() {
        return projectileSpeed;
    }

    public int getDamage() {
        return damage;
    }

    public long getCooldownTime() {
        return cooldownTime;
    }
}