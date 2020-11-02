package cc.zombies.model.agents.figures;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

public class Warrior extends SimulatedAgent {
    public Warrior(Polygon bounds, Coordinate coordinate, double speed, double angle, double awarenessRadius) {
        super("Warrior", bounds, coordinate, speed, angle, awarenessRadius);
    }

    protected void setup() {}
}
