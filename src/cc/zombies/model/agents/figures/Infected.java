package cc.zombies.model.agents.figures;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.agents.util.Cooldown;
import cc.zombies.model.behaviours.Chase;
import cc.zombies.model.behaviours.MoveAround;
import cc.zombies.model.behaviours.Sense;
import cc.zombies.model.behaviours.base.SelectiveBehaviour;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

/* Java imports */
import java.util.AbstractMap.SimpleEntry;

public class Infected extends SimulatedAgent {
    public static double DEFAULT_SPEED = 0.00025;

    public Infected(Polygon bounds, Coordinate coordinate, double speed, double angle,
                    double awarenessRadius, double actionRadius) {
        super("Infected", bounds, coordinate, speed, angle, awarenessRadius, actionRadius);
    }

    public Infected(Polygon bounds, Coordinate coordinate, double speed, double angle,
                    double awarenessRadius, double actionRadius, Cooldown senseCooldown,
                    Cooldown commsCooldown, Cooldown skillCooldown) {
        super("Infected", bounds, coordinate, speed, angle, awarenessRadius, actionRadius,
                senseCooldown, commsCooldown, skillCooldown);
    }

    protected void setup() {
        this.addBehaviour(new Sense(this, this.getSenseCooldown()));
        this.addBehaviour(new SelectiveBehaviour(this,
                            new SimpleEntry<>(this::hasScent, new Chase(this, this.getSkillCooldown())),
                            new SimpleEntry<>(this::hasNoScent, new MoveAround(this)))
        );
    }

    private boolean hasScent() {
        return this.getLastSensed().entrySet().stream().anyMatch(
                    (entry) -> !SimulatedAgent.getTypeByUuid(entry.getKey()).equalsIgnoreCase("Infected")
                                && Status.LIVE.equals(entry.getValue().getDescription()));
    }

    private boolean hasNoScent() {
        return !this.hasScent();
    }
}
