package cc.zombies.model.agents;

/* CC imports */

/* Java imports */

/* JavaFX imports */

/* JADE imports */
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

public class SimulationContainer {
    private static Runtime runtime = Runtime.instance();

    private String name;
    private ContainerController container;
    private ProfileImpl profile;

    public SimulationContainer(String name) throws ControllerException {
        this.name = name;
        this.profile = new ProfileImpl(new ExtendedProperties(new String[]{ String.format("container-name:%s", this.name) }));
        this.container = runtime.createAgentContainer(this.profile);
    }

}