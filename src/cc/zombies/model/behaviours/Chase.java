package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.agents.util.AgentPredicate;
import cc.zombies.model.behaviours.base.PeriodicBehaviour;
import cc.zombies.model.geom.internal.GeometryCalculator;

/* Java imports */
import java.util.stream.Collectors;

/* JADE imports */
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Chase extends PeriodicBehaviour {
    public Chase(SimulatedAgent agent, AgentPredicate cooldown) {
        super(agent, cooldown);
    }

    @Override
    public void action() {
        /* Get near targets */
        var scent = this.agent.getLastSensed().entrySet().stream().filter(
                (entry) -> !SimulatedAgent.getTypeByUuid(entry.getKey()).equalsIgnoreCase("Infected")
                            && SimulatedAgent.Status.LIVE.equals(entry.getValue().getDescription()))
                .collect(Collectors.toList());

        /* Chase closest target if there's such */
        if (scent.size() > 0) {

            var distances =  scent.stream().map(
                    (entry) -> GeometryCalculator.distance(entry.getValue().getCoordinate(), this.agent.getCoordinate()))
                    .collect(Collectors.toList());

            var minIndex = 0;
            for (var idx = 1; idx < distances.size(); ++idx) {
                if (distances.get(idx) < distances.get(minIndex)) {
                    minIndex = idx;
                }
            }

            var victim = scent.get(minIndex);
            if (GeometryCalculator.isPointInRadius(victim.getValue().getCoordinate(),
                    this.agent.getCoordinate(), this.agent.getActionRadius())
                    && this.cooldown.apply(this.agent)) {
                /* Infect */
                try {
                    var message = new ACLMessage(ACLMessage.INFORM);
                    message.addReceiver(new AID(
                            String.format("%s-ds", this.agent.getContainerController().getContainerName()), AID.ISLOCALNAME));
                    message.setLanguage("English");
                    message.setOntology("infect-target");
                    message.setContent(victim.getKey());
                    this.agent.send(message);
                }
                catch (Exception e) {
                    System.out.printf("Chase#action where couldn't infect target%n");
                }
            }
            else {
                /* Chase */
                this.agent.moveInDirectionOf(scent.get(minIndex).getValue().getCoordinate());
            }
        }
    }

    @Override
    public boolean done() { return !this.agent.isDead(); }
}
