package cc.zombies;

/* CC imports */
import cc.zombies.model.agents.MainContainer;
import cc.zombies.model.agents.SimulationContainer;
import cc.zombies.model.agents.figures.Infected;
import cc.zombies.model.agents.figures.Runner;
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

/* Java imports */
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/* JavaFX imports */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/* JADE imports */
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Launcher extends Application {
    // @TODO Melhorar elegância da solução
    private static class AgentPair {
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
    private StackPane layers;

    /* Jade attributes */
    SimulationContainer container;

    private int[] frames;
    private int[] frameIndex;
    private int frameCount;
    private Image runnerDefault;
    private Image[] runnersFrame;

    private MediaPlayer player;

    private void setupJade() throws Exception {
        MainContainer.start();
        this.container = new SimulationContainer("quarantine");

        var acceptedHandle = MainContainer.getInstance().getHandle().acceptNewAgent(
                                        String.format("%s-ds", this.container.getHandle().getContainerName()), this.container);
        acceptedHandle.start();
        this.container.getHandle().start();
    }

    private void setupUi(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("view/ApplicationScene.fxml"));
        loader.setController(this);
        this.root = loader.load();

        var theme = new Media(getClass().getResource("/cc/zombies/view/assets/audio/theme.mp3").toString());
        player = new MediaPlayer(theme);
        player.setVolume(0.5);
        player.play();

        primaryStage.setTitle("Zombie Virus Propagation");
        primaryStage.setScene(new Scene(root, 512, 512));
        primaryStage.show();
    }

    private void setupEvents(Stage primaryStage) {
        primaryStage.setOnCloseRequest((event) -> {
            try {
                this.container.getHandle().kill();
                MainContainer.end();
            }
            catch (Exception e) {
                System.out.println("Launcher#setupEvents where couldn't end JADE properly");
            }
        });
    }

    private void setupFigures() {
        this.runnerDefault = new Image("/cc/zombies/view/assets/img/figures/runner/runner-default-90r.png");
        this.frames = new int[] {0, 0, 0, 0};
        this.frameIndex = new int[] {0, 0, 0, 0};
        this.frameCount = 7;
        this.runnersFrame = new Image[4];

        for (var i = 0; i < this.runnersFrame.length; ++i) {
            this.runnersFrame[i] = new Image(String.format("/cc/zombies/view/assets/img/figures/runner/runner-mv-%d-90r.png", i));
        }
    }

    // @TODO Remover
    private void doTests() {
        /* Setup runners array */
        var runners = new ArrayList<AgentPair>();

        /* Setup runner spawn point: one coordinate for each unique runner */
        var coordinates = new Coordinate[] {
                Coordinate.from(1, 1), Coordinate.from(510, 1),
                Coordinate.from(510, 510), Coordinate.from(1, 510)
        };

        for (var idx = 0; idx < coordinates.length; ++idx) {
            this.layers.getChildren().add(new Canvas(512, 512));
        }

        /* Instantiate runners and controllers */
        for (int i = 0; i < coordinates.length; ++i) {
            try {
                var runner = new Runner(
                        Polygon.from(0, 0, 512, 0, 512, 512, 0, 512, 0, 0), coordinates[i], 0.0002,
                        0.0, 512 * 0.2, 0.0, SimulatedAgent.invalidateByCount(1.0)
                );
                var controller = container.getHandle().acceptNewAgent(runner.getUuid(), runner);

                runners.add(new AgentPair(runner, controller));

                this.container.registerAgent(runner);
            } catch (StaleProxyException e) {
                System.out.println("Exception while trying to accept new agent");
            }
        }

        SimulatedAgent infected = null;
        AgentController itsController = null;
        try {
            infected = new Infected(
                    Polygon.from(0, 0, 512, 0, 512, 512, 0, 512, 0, 0), new Coordinate(256, 256), 0.0004,
                    0.0, 512 * 0.2, 0.0, SimulatedAgent.invalidateByCount(1.0)
            );
            itsController = container.getHandle().acceptNewAgent(infected.getUuid(), infected);
        }
        catch (Exception e) {
            System.out.println("Exception while trying to launch fixed infected");
        }


        /* Start UI update background thread */
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    for (var idx = 1; idx < layers.getChildren().size(); ++idx) {
                        var gc = ((Canvas) layers.getChildren().get(idx)).getGraphicsContext2D();

                        gc.clearRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getWidth());
                        gc.setFill(Color.LIGHTPINK);

                        var agent = runners.get(idx - 1).agent;

                        double x = agent.getCoordinate().getX(), y = agent.getCoordinate().getY();
                        gc.save();
                        var r = new Rotate(agent.getAngle(), x, y);
                        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                        //gc.fillRect(x - 7.5, y - 7.5, 15, 15);

                        frames[idx - 1]++;
                        if (frames[idx - 1] % frameCount == 0) {
                            frameIndex[idx - 1] = (4 + frameIndex[idx - 1] + 1) % 4;
                        }

                        gc.drawImage(runnersFrame[frameIndex[idx - 1]], x - 16, y - 16);
                        gc.restore();

                        //gc.fillOval(agent.getCoordinate().getX() - 15, agent.getCoordinate().getY() - 15, 15 ,15);
                    }
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

        try { itsController.start(); container.registerAgent(infected); } catch (Exception e) { e.printStackTrace(); };
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* Setup JADE in another Thread */
        var jadeJob = new Thread(() -> {
            try {
                this.setupJade();
            }
            catch (Exception ignore) {
                System.out.printf("Launcher#start where setupJade failed%n");
            }
        });
        jadeJob.start();

        /* Setup User UI */
        this.setupUi(primaryStage);
        this.setupEvents(primaryStage);
        this.setupFigures();

        /* Run tests */
        new Thread(() -> {
            try {
                jadeJob.join();
                Platform.runLater(this::doTests);
            }
            catch (Exception e) {
                System.out.printf("Launcher#start where tests failed%n{%n%s%n}", e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
