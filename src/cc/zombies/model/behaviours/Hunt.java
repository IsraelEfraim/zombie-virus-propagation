package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;

/* JADE imports */
import jade.core.behaviours.Behaviour;

/* @TODO Implementar Hunt
 *
 * hunt() -> void; moves in direction of enemy
 * slay() -> void; if position in sight
 */
public class Hunt extends Behaviour {
    private SimulatedAgent agent;

    public Hunt(SimulatedAgent a) {
        super(a);
        this.agent = a;
    }

    @Override
    public void action() {

    }

    @Override
    public boolean done() {
        return false;
    }
}
