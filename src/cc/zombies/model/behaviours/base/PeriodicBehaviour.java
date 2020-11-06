package cc.zombies.model.behaviours.base;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.agents.util.Cooldown;

public abstract class PeriodicBehaviour extends SimulationBehaviour {
    protected final Cooldown cooldown;

    public PeriodicBehaviour(SimulatedAgent agent, Cooldown cooldown) {
        super(agent);
        this.cooldown = cooldown;
    }
}
