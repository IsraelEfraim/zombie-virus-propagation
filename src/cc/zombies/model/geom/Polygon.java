package cc.zombies.model.geom;

public class Polygon {
    private Coordinate[] vertex ;

    public Polygon(Coordinate[] vertex) {
        this.setVertex(vertex);
    }

    public Coordinate[] getVertex() {
        return vertex;
    }

    public void setVertex(Coordinate[] vertex) {
        this.vertex = vertex;
    }

    // @TODO: Implementar calculo de geometria.
    public boolean isWithin(Coordinate coordinate) {
        return vertex.length == 0 ? false : true;
    }

    public static Polygon from(Coordinate... vertex) {
        return new Polygon(vertex);
    }

    public static Polygon from(double... values) {
        if (values.length % 2 == 0) {
            Coordinate[] coordinates = new Coordinate[values.length / 2];

            for (int i = 0; i < values.length / 2; i += 2) {
                coordinates[i / 2] = new Coordinate(values[i], values[i + 1]);
            }

            return new Polygon(coordinates);
        }
        else {
            throw new RuntimeException("Polygon#from with odd number of parameters");
        }
    }
}
