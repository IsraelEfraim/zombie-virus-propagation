package cc.zombies.model.agents.util;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;

public interface Cooldown {
    boolean check(SimulatedAgent agent);
    void use();
}
