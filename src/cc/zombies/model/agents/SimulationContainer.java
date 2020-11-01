package cc.zombies.model.agents;

/* JADE imports */
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class SimulationContainer {
    private static Runtime runtime = Runtime.instance();

    private String name;
    private AgentContainer container;
    private ProfileImpl profile;

    public SimulationContainer(String name) throws ControllerException {
        this.name = name;
        this.profile = new ProfileImpl(new ExtendedProperties(new String[]{ "container-name:".concat(this.name) }));
        this.container = runtime.createAgentContainer(this.profile);
    }

    public AgentContainer getContainer() {
        return this.container;
    }
}