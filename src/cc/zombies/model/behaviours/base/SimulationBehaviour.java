package cc.zombies.model.behaviours.base;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;

/* JADE imports */
import jade.core.behaviours.Behaviour;

public abstract class SimulationBehaviour extends Behaviour {
    protected final SimulatedAgent agent;

    public SimulationBehaviour(SimulatedAgent agent) {
        super(agent);
        this.agent = agent;
    }
}
