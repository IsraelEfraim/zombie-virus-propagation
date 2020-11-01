package cc.zombies.model.geom;

public class Coordinate {
    private double x;
    private double y;

    public Coordinate(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static Coordinate from(double x, double y) {
        return new Coordinate(x, y);
    }
}
