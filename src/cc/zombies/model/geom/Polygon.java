package cc.zombies.model.geom;

/* CC imports */
import cc.zombies.model.geom.internal.GeometryCalculator;

/* Java imports */
import java.util.Arrays;

public class Polygon {
    private Coordinate[] vertex;

    public Polygon(Coordinate[] vertex) {
        this.setVertex(vertex);
    }

    public Coordinate[] getVertex() {
        return vertex;
    }

    public void setVertex(Coordinate[] vertex) {
        this.vertex = vertex;
    }

    public static Polygon from(Coordinate... vertex) {
        return new Polygon(vertex);
    }

    public boolean contains(Coordinate coordinate) {
        return GeometryCalculator.isPointInBounds(coordinate, this.vertex);
    }

    public static Polygon from(double... values) {
        if (values.length % 2 == 0) {
            Coordinate[] coordinates = new Coordinate[values.length / 2];

            for (int i = 0; i < values.length; i += 2) {
                coordinates[i / 2] = new Coordinate(values[i], values[i + 1]);
            }

            return new Polygon(coordinates);
        }
        else {
            throw new RuntimeException("Polygon#from with odd number of parameters");
        }
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "vertex=" + Arrays.toString(vertex) +
                '}';
    }
}
