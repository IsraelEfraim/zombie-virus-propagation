package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.agents.util.AgentPredicate;
import cc.zombies.model.behaviours.base.PeriodicBehaviour;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.TimedCoordinate;

/* JADE imports */
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Sense extends PeriodicBehaviour {
    public Sense(SimulatedAgent agent, AgentPredicate cooldown) {
        super(agent, cooldown);
    }

    @Override
    public void action() {
        var message = this.agent.receive();

        if (message != null) {
            var content = message.getContent();

            if (message.getOntology().equalsIgnoreCase("reply:query-nearby")) {
                var lastSensed = this.agent.getLastSensed();
                lastSensed.clear();

                var sensed = this.agent.getSensed();

                var lines = content.split(",");
                for (var line : lines) {
                    if (!line.isBlank()) {
                        /* Message pattern is "uuid x y status epoch"*/
                        var args = line.split(" ");

                        var epoch = Long.parseLong(args[4]);
                        var tc = new TimedCoordinate(new Coordinate(Double.parseDouble(args[1]),
                                                        Double.parseDouble(args[2])), args[3], epoch);

                        lastSensed.put(args[0], tc);

                        if (sensed.containsKey(args[0])) {
                            var last = sensed.get(args[0]);

                            if (last.getEpoch() < epoch) {
                                sensed.put(args[0], tc);
                            }
                        }
                        else {
                            sensed.put(args[0], tc);
                            System.out.printf("[%s] knows about {%s}%n", this.agent.getUuid(), args[0]);
                        }
                    }
                }
            }
        }
        else {
            if (this.cooldown.apply(this.agent)) {
                try {
                    var request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(new AID(
                            String.format("%s-ds", this.agent.getContainerController().getContainerName()), AID.ISLOCALNAME));
                    request.setLanguage("English");
                    request.setOntology("query-nearby");
                    request.setContent(String.format("%.8f %.8f %.8f", this.agent.getCoordinate().getX(),
                            this.agent.getCoordinate().getY(), this.agent.getAwarenessRadius()));
                    this.agent.send(request);
                }
                catch (Exception e) {
                    System.out.printf("Sense#action where couldn't sent message to container ds%n");
                }
            }
        }
    }

    @Override
    public boolean done() { return false; }
}
