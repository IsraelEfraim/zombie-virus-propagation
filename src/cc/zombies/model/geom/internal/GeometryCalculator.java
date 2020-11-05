package cc.zombies.model.geom.internal;

/* CC imports */
import cc.zombies.model.geom.Coordinate;

/* JavaFX imports */
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public abstract class GeometryCalculator {
    private static double[] coordinatesToDoubleArray(Coordinate[] bounds) {
        var points = new double[bounds.length * 2];

        for (int i = 0; i < bounds.length * 2; i += 2) {
            points[i] = bounds[i / 2].getX();
            points[i + 1] = bounds[i / 2].getY();
        }

        return points;
    }

    public static boolean isPointInBounds(Coordinate point, Coordinate[] bounds) {
        var p = new Polygon(GeometryCalculator.coordinatesToDoubleArray(bounds));

        return p.contains(point.getX(), point.getY());
    }

    public static boolean isPointInRadius(Coordinate point, Coordinate target, double radius) {
        var c = new Circle(target.getX(), target.getY(), radius);

        return c.contains(point.getX(), point.getY());
    }

    public static double distance(Coordinate p1, Coordinate p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2)
                            + Math.pow(p1.getY() - p2.getY(), 2));
    }
}
