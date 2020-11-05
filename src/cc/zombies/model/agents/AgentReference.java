package cc.zombies.model.agents;

/* JADE imports */
import jade.core.Agent;
import jade.wrapper.AgentController;

public class AgentReference <AgentType extends Agent> {
    private final AgentType agent;
    private final AgentController controller;

    public AgentReference(AgentType agent, AgentController controller) {
        this.agent = agent;
        this.controller = controller;
    }

    public AgentType getAgent() {
        return agent;
    }

    public AgentController getController() {
        return controller;
    }
}
