package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.behaviours.base.PeriodicBehaviour;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.TimedCoordinate;

/* Java imports */
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADE imports */
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Communicate extends PeriodicBehaviour {
    public Communicate(SimulatedAgent agent, Function<SimulatedAgent, Boolean> invalidated) {
        super(agent, invalidated);
    }

    @Override
    public void action() {
        var message = this.myAgent.receive();

        if (message != null) {
            var content = message.getContent();

            if (message.getOntology().equalsIgnoreCase("threat-warning")) {
                var sensed = this.agent.getSensed();

                /* Message pattern "[uuid x y epoch,]" */
                var lines = content.split(",");
                for (var line : lines) {
                    if (!line.isBlank()) {
                        /* Message pattern is "uuid x y epoch"*/
                        var args = line.split(" ");

                        var epoch = Long.parseLong(args[3]);
                        var tc = new TimedCoordinate(new Coordinate(Double.parseDouble(args[1]),
                                Double.parseDouble(args[2])), epoch);

                        if (sensed.containsKey(args[0])) {
                            var last = sensed.get(args[0]);

                            if (last.getEpoch() < epoch) {
                                sensed.put(args[0], tc);
                            }
                        }
                        else {
                            sensed.put(args[0], tc);
                        }

                        // System.out.printf("{%s} now knows about %s%n", this.agent.getUuid(), args[0], tc);
                    }
                }
            }
        }

        if (this.invalidated.apply(this.agent)) {
            var threats = this.agent.getSensed().entrySet().stream().filter(
                    (entry) -> SimulatedAgent.getTypeByUuid(entry.getKey()).equalsIgnoreCase("Infected"))
                                    .collect(Collectors.toList()
            );

            if (!threats.isEmpty()) {
                var builder = new StringBuilder();
                threats.forEach((entry) -> {
                    builder.append(
                            /* Sent pattern is "uuid x y epoch" */
                            String.format("%s %.8f %.8f %d,", entry.getKey(), entry.getValue().getCoordinate().getX(),
                                    entry.getValue().getCoordinate().getY(), entry.getValue().getEpoch())
                    );
                });
                var knownThreats = builder.toString();

                var allies = this.agent.getLastSensed().entrySet().stream().filter(
                        (entry) -> !SimulatedAgent.getTypeByUuid(entry.getKey()).equalsIgnoreCase("Infected"))
                        .collect(Collectors.toList()
                );

                // System.out.printf("{%s} sees %d allies at the moment%n", this.agent.getUuid(), allies.size());

                for (var ally : allies) {
                    var warning = new ACLMessage(ACLMessage.INFORM);
                    warning.addReceiver(new AID(ally.getKey(), AID.ISLOCALNAME));
                    warning.setLanguage("English");
                    warning.setOntology("threat-warning");
                    warning.setContent(knownThreats);

                    this.myAgent.send(warning);

                    System.out.printf("{%s} Warned {%s} about threat%n", this.agent.getUuid(), ally.getKey());
                }
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
