package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;

/* JADE imports */
import jade.core.behaviours.Behaviour;

/* @TODO Implementar Communicate
 *
 * [ uuid, position, epoch ]
 * maybe put this in an extended class of a SimulatedAgent
 *
 * queryNearby(String type) -> Map<String, Coordinate>
 * into
 * queryNearbyThreats() -> Map<String, Coordinate>
 * queryNearbyAllies() -> Map<String, Coordinate>
 *
 * send Threats collection to each Ally
 *
 * on receive
 * Replace each uuid with new position if receiving epoch > epoch
 * Add non-existing uuid
 *
 * epoch = System.currentTimeMillis;
 */
public class Communicate extends Behaviour {
    private final SimulatedAgent agent;

    public Communicate(SimulatedAgent a) {
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
