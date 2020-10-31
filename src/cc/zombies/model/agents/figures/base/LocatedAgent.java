package cc.zombies.model.agents.figures.base;

/* CC imports */
import cc.zombies.model.geom.Coordinate;

/* Java imports */

/* JavaFX imports */

/* JADE imports */
import jade.core.Agent;

public abstract class LocatedAgent extends Agent {
    protected Coordinate coordinate;

    public LocatedAgent(Coordinate coordinate) {
        this.setCoordinate(coordinate);
    }

    public LocatedAgent() {
        this.setCoordinate(new Coordinate(0,0));
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

}
