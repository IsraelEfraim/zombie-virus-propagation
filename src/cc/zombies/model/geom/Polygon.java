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
}
