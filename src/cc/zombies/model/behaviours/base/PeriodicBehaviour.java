package cc.zombies.model.behaviours.base;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;

/* Java imports */
import java.util.function.Function;

public abstract class PeriodicBehaviour extends SimulationBehaviour {
    protected final Function<SimulatedAgent, Boolean> invalidated;

    public PeriodicBehaviour(SimulatedAgent agent, Function<SimulatedAgent, Boolean> invalidated) {
        super(agent);
        this.invalidated = invalidated;
    }
}
