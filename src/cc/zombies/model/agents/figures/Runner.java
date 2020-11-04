package cc.zombies.model.agents.figures;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.behaviours.Communicate;
import cc.zombies.model.behaviours.Sense;
import cc.zombies.model.behaviours.MoveAround;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

/* Java imports */
import java.util.function.Function;

// @TODO Modificar a classe
public class Runner extends SimulatedAgent {
    public Runner(Polygon bounds, Coordinate coordinate, double speed, double angle, double awarenessRadius,
                    double actionRadius, Function<SimulatedAgent, Boolean> invalidation) {
        super("Runner", bounds, coordinate, speed, angle, awarenessRadius, actionRadius, invalidation);
    }

    protected void setup() {
        this.addBehaviour(new Sense(this, this.getInvalidated()));
        this.addBehaviour(new Communicate(this, this.getInvalidated()));
        this.addBehaviour(new MoveAround(this));
    }
}
