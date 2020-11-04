package cc.zombies.model.agents;

/* JADE imports */
import jade.core.Agent;
import jade.wrapper.AgentController;

public class AgentReference <A extends Agent> {
    private final A agent;
    private final AgentController controller;

    public AgentReference(A agent, AgentController controller) {
        this.agent = agent;
        this.controller = controller;
    }

    public A getAgent() {
        return agent;
    }

    public AgentController getController() {
        return controller;
    }
}
