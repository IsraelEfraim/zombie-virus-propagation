package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.behaviours.base.SimulationBehaviour;
import cc.zombies.model.geom.internal.GeometryCalculator;

/* Java imports */
import java.util.stream.Collectors;

/* JADE imports */
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Hunt extends SimulationBehaviour {
    public Hunt(SimulatedAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        /* Get all sensed targets (is infected and is alive) */
        var knownTargets = this.agent.getSensed().entrySet().stream().filter(
                (entry) -> SimulatedAgent.getTypeByUuid(entry.getKey()).equalsIgnoreCase("Infected")
                        && SimulatedAgent.Status.LIVE.equalsIgnoreCase(entry.getValue().getDescription()))
                .collect(Collectors.toList()
        );

        /* Get only the last sensed targets (current in range) */
        var seenTargets = this.agent.getLastSensed().entrySet().stream().filter(
                (entry) -> SimulatedAgent.getTypeByUuid(entry.getKey()).equalsIgnoreCase("Infected")
                        && SimulatedAgent.Status.LIVE.equalsIgnoreCase(entry.getValue().getDescription()))
                .collect(Collectors.toList()
        );

        /* Update each known target supposed to be near that isn't currently in range */
        for (var target : knownTargets) {
            if (GeometryCalculator.isPointInRadius(target.getValue().getCoordinate(),
                    this.agent.getCoordinate(), this.agent.getSpeed())) {
                var isSeen = seenTargets.stream().anyMatch(
                        (entry) -> entry.getKey().equals(target.getKey()));

                if (!isSeen) {
                    target.getValue().setDescription(SimulatedAgent.Status.MISSING);
                }
            }
        }

        var validTargets = knownTargets.stream().filter(
                (entry) -> SimulatedAgent.Status.LIVE.equalsIgnoreCase(entry.getValue().getDescription()))
                .collect(Collectors.toList());

        /* Chase closest valid target */
        if (validTargets.size() > 0) {
            var distances =  validTargets.stream().map(
                    (entry) -> GeometryCalculator.distance(entry.getValue().getCoordinate(), this.agent.getCoordinate()))
                    .collect(Collectors.toList());

            var minIndex = 0;
            for (var idx = 1; idx < distances.size(); ++idx) {
                if (distances.get(idx) < distances.get(minIndex)) {
                    minIndex = idx;
                }
            }

            var threat = validTargets.get(minIndex);
            if (GeometryCalculator.isPointInRadius(threat.getValue().getCoordinate(),
                    this.agent.getCoordinate(), this.agent.getActionRadius())
                    && this.agent.getSkillCooldown().apply(this.agent)) {
                /* Slay */
                try {
                    var message = new ACLMessage(ACLMessage.INFORM);
                    message.addReceiver(new AID(
                            String.format("%s-ds", this.agent.getContainerController().getContainerName()), AID.ISLOCALNAME));
                    message.setLanguage("English");
                    message.setOntology("slay-target");
                    message.setContent(threat.getKey());
                    this.agent.send(message);
                }
                catch (Exception e) {
                    System.out.printf("Hunt#action where couldn't slay target%n");
                }
            }
            else {
                this.agent.moveInDirectionOf(validTargets.get(minIndex).getValue().getCoordinate());
            }
        }
    }

    @Override
    public boolean done() { return !this.agent.isDead(); }
}
