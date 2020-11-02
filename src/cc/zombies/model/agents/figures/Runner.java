package cc.zombies.model.agents.figures;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.behaviours.MoveAround;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

// @TODO Modificar a classe
public class Runner extends SimulatedAgent {
    public Runner(Polygon bounds, Coordinate coordinate, double speed, double angle, double awarenessRadius) {
        super("Runner", bounds, coordinate, speed, angle, awarenessRadius);
    }

    protected void setup() {
        this.addBehaviour(new MoveAround(this));
    }
}
