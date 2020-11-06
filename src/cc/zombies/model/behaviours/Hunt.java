package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.agents.util.Cooldown;
import cc.zombies.model.behaviours.base.PeriodicBehaviour;
import cc.zombies.model.geom.internal.GeometryCalculator;

/* Java imports */
import java.util.stream.Collectors;

/* JADE imports */
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Hunt extends PeriodicBehaviour {
    private String activeTargetUuid;
    private Cooldown killCooldown;

    public Hunt(SimulatedAgent agent, Cooldown cooldown) {
        super(agent, cooldown);
        this.activeTargetUuid = null;
        this.killCooldown = SimulatedAgent.countCooldown(15.0);
    }

    @Override
    public void action() {
        if (this.cooldown.check(this.agent)) {
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
                        this.agent.getCoordinate(), this.agent.getAwarenessRadius() * 0.5)) {
                    var isSeen = seenTargets.stream().anyMatch(
                            (entry) -> entry.getKey().equals(target.getKey()));

                    if (!isSeen) {
                        target.getValue().setDescription(SimulatedAgent.Status.MISSING);
                        System.out.printf("[%s] didn't find {%s}%n", this.agent.getUuid(), target.getKey());
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

                this.activeTargetUuid = validTargets.get(minIndex).getKey();
            }

            this.cooldown.use();
        }

        var readyToKill = this.killCooldown.check(this.agent);

        if (this.activeTargetUuid != null) {
            var threat = this.agent.getSensed().get(this.activeTargetUuid);

            this.agent.moveInDirectionOf(threat.getCoordinate());

            if (readyToKill && GeometryCalculator.isPointInRadius(threat.getCoordinate(),
                    this.agent.getCoordinate(), this.agent.getActionRadius())) {
                /* Slay */
                try {
                    var message = new ACLMessage(ACLMessage.INFORM);
                    message.addReceiver(new AID(
                            String.format("%s-ds", this.agent.getContainerController().getContainerName()), AID.ISLOCALNAME));
                    message.setLanguage("English");
                    message.setOntology("slay-target");
                    message.setContent(this.activeTargetUuid);
                    this.agent.send(message);

                    this.activeTargetUuid = null;
                    this.killCooldown.use();
                }
                catch (Exception e) {
                    System.out.printf("Hunt#action where couldn't slay target%n");
                }
            }
        }
    }

    @Override
    public boolean done() { return this.agent.isDead(); }
}
