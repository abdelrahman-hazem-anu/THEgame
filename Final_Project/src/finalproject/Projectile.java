package finalproject;

public class Projectile {
    private double x, y;
    private final double speed; 
    private final int damage;
    private final double directionX, directionY; 

    public Projectile(double x, double y, double directionX, double directionY, double speed, int damage) {
        this.x = x;
        this.y = y;
        
        double len = Math.sqrt(directionX * directionX + directionY * directionY);
        if (len == 0) {
            this.directionX = 1;
            this.directionY = 0;
        } else {
            this.directionX = directionX / len;
            this.directionY = directionY / len;
        }
        
        this.speed = speed;
        this.damage = damage;
    }

    public void updatePosition() {
        x += directionX * speed;
        y += directionY * speed;
    }

    public double getX() { 
        return x;
    }
    
    public double getY() {
        return y;
    }

    public int getDamage() {
        return damage;
    }

    public double getDirectionX() {
        return directionX;
    }
}