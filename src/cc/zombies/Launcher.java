package cc.zombies;

/* CC imports */
import cc.zombies.model.agents.MainContainer;
import cc.zombies.model.agents.SimulationContainer;
import cc.zombies.model.agents.figures.Runner;
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

/* Java imports */
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/* JavaFX imports */
import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/* JADE imports */
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Launcher extends Application {
    // @TODO Melhorar elegância da solução
    private class AgentPair {
        public SimulatedAgent agent;
        public AgentController controller;

        public AgentPair(SimulatedAgent agent, AgentController controller) {
            this.agent = agent;
            this.controller = controller;
        }
    }

    /* JavaFX attributes */

    private Parent root;
    
    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    /* Jade attributes */
    SimulationContainer container;

    private void setupJade() throws Exception {
        MainContainer.start();
        this.container = new SimulationContainer("zombie-virus-propagation");
        this.container.getAgentContainer().start();
    }

    private void setupUi(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("view/ApplicationScene.fxml"));
        loader.setController(this);
        this.root = loader.load();

        primaryStage.setTitle("Zombie Virus Propagation");
        primaryStage.setScene(new Scene(root, 512, 512));
        primaryStage.show();

        this.gc = canvas.getGraphicsContext2D();
    }

    private void setupEvents(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest((event) -> {
            try {
                this.container.getAgentContainer().kill();
                MainContainer.end();
            }
            catch (Exception e) {
                System.out.println("Launcher#setupEvents where couldn't end JADE properly");
            }
        });
    }

    // @TODO Remover
    private void doTests() {
        /* Setup runners array */
        var runners = new ArrayList<AgentPair>();

        /* Setup runner spawn point: one coordinate for each unique runner */
        var coordinates = new Coordinate[] {
                Coordinate.from(256, 256), Coordinate.from(100, 100),
                Coordinate.from(70, 80), Coordinate.from(400, 300)
        };

        /* Instantiate runners and controllers */
        for (int i = 0; i < coordinates.length; ++i) {
            try {
                var runner = new Runner(
                        Polygon.from(0, 0, 512, 0, 512, 512, 0, 512, 0, 0),
                        coordinates[i], 0.0005, 0.0, 512 * 0.2
                );
                var controller = container.getAgentContainer().acceptNewAgent("Runner" + i, runner);

                runners.add(new AgentPair(runner, controller));
            } catch (StaleProxyException e) {
                System.out.println("Exception while trying to accept new agent");
            }
        }

        /* Start UI update background thread */
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
                    gc.setFill(Color.LIGHTPINK);

                    runners.forEach((pair) -> {
                        var agent = pair.agent;
                        gc.fillOval(agent.getCoordinate().getX(), agent.getCoordinate().getY(),
                                15 ,15);
                    });
                });
            }
        }, 0, 30);

        /* Dispatch controllers start */
        runners.forEach((pair) -> {
            try {
                pair.controller.start();
            }
            catch (Exception ignore) {}
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* Setup JADE in another Thread */
        var jadeJob = new Thread(() -> {
            try {
                this.setupJade();
            }
            catch (Exception ignore) {}
        });
        jadeJob.start();

        /* Setup User UI */
        this.setupUi(primaryStage);

        /* Run tests */
        new Thread(() -> {
            try {
                jadeJob.join();
                this.doTests();
            }
            catch (Exception e) {
                System.out.println("Launcher#start where jadeJob thread failed");
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
