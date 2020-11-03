package cc.zombies.model.agents;

/* JADE imports */
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;

public class SimulationContainer {
    private static final Runtime runtime = Runtime.instance();

    private final String name;
    private final AgentContainer container;
    private final ProfileImpl profile;

    public SimulationContainer(String name) {
        this.name = name;
        this.profile = new ProfileImpl(new ExtendedProperties(new String[]{ "container-name:".concat(this.name) }));
        this.container = runtime.createAgentContainer(this.profile);
    }

    public AgentContainer getHandle() {
        return this.container;
    }
}