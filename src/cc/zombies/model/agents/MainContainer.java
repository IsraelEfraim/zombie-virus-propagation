package cc.zombies.model.agents;

/* JADE imports */
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MainContainer {
    private static final Runtime runtime = Runtime.instance();
    private static MainContainer instance;

    private final AgentContainer container;

    private MainContainer() throws ControllerException {
        var profile = new ProfileImpl(new ExtendedProperties(new String[]{ "gui:false" }));
        this.container = runtime.createMainContainer(profile);
        this.container.start();
    }

    private static void setInstance(MainContainer instance) {
        MainContainer.instance = instance;
    }

    public static MainContainer getInstance() throws ControllerException {
        if (instance == null) {
            MainContainer.setInstance(new MainContainer());
        }
        return MainContainer.instance;
    }

    public AgentContainer getHandle() {
        return this.container;
    }

    public static void start() throws ControllerException {
        MainContainer.getInstance();
    }

    public static void end() throws ControllerException {
        if (MainContainer.instance != null) {
            MainContainer.getInstance().container.kill();
            MainContainer.runtime.shutDown();
        }
    }
}