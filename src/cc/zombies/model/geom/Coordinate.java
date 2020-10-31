package cc.zombies.model.geom;

public class Coordinate {
    private double x;
    private double y;

    public Coordinate(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    private double getX() {
        return this.x;
    }

    private double getY() {
        return this.y;
    }

    private void setX(double x) {
        this.x = x;
    }

    private void setY(double y) {
        this.y = y;
    }
}
