package cc.zombies.model.agents.figures;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.agents.util.Cooldown;
import cc.zombies.model.behaviours.Communicate;
import cc.zombies.model.behaviours.Sense;
import cc.zombies.model.behaviours.MoveAround;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

public class Runner extends SimulatedAgent {
    public static double DEFAULT_SPEED = 0.00025;

    public Runner(Polygon bounds, Coordinate coordinate, double speed, double angle,
                    double awarenessRadius, double actionRadius) {
        super("Runner", bounds, coordinate, speed, angle, awarenessRadius, actionRadius);
    }

    public Runner(Polygon bounds, Coordinate coordinate, double speed, double angle,
                  double awarenessRadius, double actionRadius, Cooldown senseCooldown,
                  Cooldown commsCooldown, Cooldown skillCooldown) {
        super("Runner", bounds, coordinate, speed, angle, awarenessRadius, actionRadius,
                senseCooldown, commsCooldown, skillCooldown);
    }

    protected void setup() {
        this.addBehaviour(new Sense(this, this.getSenseCooldown()));
        this.addBehaviour(new Communicate(this, this.getCommsCooldown()));
        this.addBehaviour(new MoveAround(this));
    }
}
