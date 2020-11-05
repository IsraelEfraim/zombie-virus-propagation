package cc.zombies.model.agents.util;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;

@FunctionalInterface
public interface AgentPredicate {
    boolean apply(SimulatedAgent agent);
}
