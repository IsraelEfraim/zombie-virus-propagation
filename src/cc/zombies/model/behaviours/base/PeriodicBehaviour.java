package cc.zombies.model.behaviours.base;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.agents.util.AgentPredicate;

public abstract class PeriodicBehaviour extends SimulationBehaviour {
    protected final AgentPredicate cooldown;

    public PeriodicBehaviour(SimulatedAgent agent, AgentPredicate cooldown) {
        super(agent);
        this.cooldown = cooldown;
    }
}
