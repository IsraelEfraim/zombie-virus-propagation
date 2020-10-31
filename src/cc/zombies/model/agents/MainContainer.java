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

public class MainContainer {
    private static Runtime runtime = Runtime.instance();
    private static MainContainer instance;

    private ContainerController container;
    private ProfileImpl profile;

    private MainContainer() throws ControllerException {
        this.profile = new ProfileImpl(new ExtendedProperties(new String[]{ "gui:true" }));
        this.container = runtime.createMainContainer(this.profile);
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

    public static void start() throws ControllerException {
        MainContainer.getInstance();
    }
}