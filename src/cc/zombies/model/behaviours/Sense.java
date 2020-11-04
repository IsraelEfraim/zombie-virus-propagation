package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.behaviours.base.PeriodicBehaviour;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.TimedCoordinate;

/* Java imports */
import java.util.function.Function;

/* JADE imports */
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/* @TODO Implementar Communicate
 *
 * [ uuid -> { position, epoch } ]
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
public class Sense extends PeriodicBehaviour {
    public Sense(SimulatedAgent agent, Function<SimulatedAgent, Boolean> invalidated) {
        super(agent, invalidated);
    }

    @Override
    public void action() {
        var message = this.myAgent.receive();

        if (message != null) {
            var content = message.getContent();

            if (message.getOntology().equalsIgnoreCase("reply:query-nearby")) {
                var lastSensed = this.agent.getLastSensed();
                lastSensed.clear();

                var sensed = this.agent.getSensed();

                var lines = content.split(",");
                for (var line : lines) {
                    if (!line.isBlank()) {
                        /* Message pattern is "uuid x y epoch"*/
                        var args = line.split(" ");

                        var epoch = Long.parseLong(args[3]);
                        var tc = new TimedCoordinate(new Coordinate(Double.parseDouble(args[1]),
                                                        Double.parseDouble(args[2])), epoch);

                        lastSensed.put(args[0], tc);

                        if (sensed.containsKey(args[0])) {
                            var last = sensed.get(args[0]);

                            if (last.getEpoch() < epoch) {
                                sensed.put(args[0], tc);
                            }
                        }
                        else {
                            sensed.put(args[0], tc);
                        }

                        //System.out.printf("{%s} read %s -> %s%n", this.agent.getUuid(), args[0], tc);
                    }
                }

                // System.out.printf("{%s} senses %d nearby agents%n", this.agent.getUuid(), this.agent.getLastSensed().size());
            }
        }
        else {
            if (this.invalidated.apply(this.agent)) {
                try {
                    var request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(new AID(
                            String.format("%s-ds", this.myAgent.getContainerController().getContainerName()), AID.ISLOCALNAME));
                    request.setLanguage("English");
                    request.setOntology("query-nearby");
                    request.setContent(String.format("%.8f %.8f %.8f", this.agent.getCoordinate().getX(),
                            this.agent.getCoordinate().getY(), this.agent.getAwarenessRadius()));
                    this.myAgent.send(request);

                    //System.out.printf("Sent request to %s%n", String.format("%s-ds", this.myAgent.getContainerController().getContainerName()));
                }
                catch (Exception e) {
                    System.out.printf("Sense#action where couldn't sent message to container ds%n");
                }
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
