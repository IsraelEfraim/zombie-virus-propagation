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

    @Override
    public String toString() {
        return String.format("Coordinate{x:%.5f,y:%.5f}", x, y);
    }

    public boolean equals(Coordinate other) {
        return this.getX() == other.getX() && this.getY() == other.getY();
    }
}
