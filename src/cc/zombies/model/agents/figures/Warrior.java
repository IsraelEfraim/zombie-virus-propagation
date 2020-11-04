package cc.zombies.model.agents.figures;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

/* Java imports */
import java.util.function.Function;

public class Warrior extends SimulatedAgent {
    public Warrior(Polygon bounds, Coordinate coordinate, double speed, double angle, double awarenessRadius,
                   double actionRadius, Function<SimulatedAgent, Boolean> invalidated) {
        super("Warrior", bounds, coordinate, speed, angle, awarenessRadius, actionRadius, invalidated);

    }

    protected void setup() {}
}
