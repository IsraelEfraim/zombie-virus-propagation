package cc.zombies;

/* CC imports */
import cc.zombies.control.FigureFrameController;
import cc.zombies.control.FigureFrameDispatcher;
import cc.zombies.model.agents.MainContainer;
import cc.zombies.model.agents.SimulationManager;
import cc.zombies.model.agents.figures.Infected;
import cc.zombies.model.agents.figures.Runner;
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;
import cc.zombies.model.random.RandomHelper;

/* Java imports */
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import javafx.util.Duration;

public class Launcher extends Application {
    /* JavaFX attributes */

    private Parent root;
    private MediaPlayer player;

    @FXML
    private StackPane layers;

    /* Jade attributes */

    SimulationManager manager;

    private FigureFrameDispatcher frameDispatcher;

    private void setupJade() throws Exception {
        MainContainer.start();
        this.manager = new SimulationManager("quarantine");
        this.manager.getController().start();
        this.manager.getContainer().start();
    }

    private void setupUi(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("view/ApplicationScene.fxml"));
        loader.setController(this);
        this.root = loader.load();

        var theme = new Media(getClass().getResource("/cc/zombies/view/assets/audio/theme.mp3").toString());
        player = new MediaPlayer(theme);
        player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        player.setVolume(0.5);
        player.play();

        primaryStage.setTitle("Zombie Virus Propagation");
        primaryStage.setScene(new Scene(root, 512, 512));
        primaryStage.show();
    }

    private void setupEvents(Stage primaryStage) {
        primaryStage.setOnCloseRequest((event) -> {
            try {
                this.manager.getContainer().kill();
                MainContainer.end();
            }
            catch (Exception e) {
                System.out.println("Launcher#setupEvents where couldn't end JADE properly");
            }
        });
    }

    private void setupFigures() {
        this.frameDispatcher = new FigureFrameDispatcher(SimulatedAgent::getTypeByUuid);

        this.frameDispatcher.addDispatcher(
                "Runner",
                new FigureFrameController(7, IntStream.range(0, 4).mapToObj(
                    (idx) -> new Image(String.format("/cc/zombies/view/assets/img/figures/runner/mv-%d-90r.png", idx))
                ).collect(Collectors.toList()))
        );

        this.frameDispatcher.addDispatcher(
                "Infected",
                new FigureFrameController(7, IntStream.range(0, 4).mapToObj(
                    (idx) -> new Image(String.format("/cc/zombies/view/assets/img/figures/infected/mv-%d-90r.png", idx))
                ).collect(Collectors.toList()))
        );
    }

    // @TODO Remover
    private void doTests() {
        var bounds = Polygon.from(0, 0, 512, 0, 512, 512, 0, 512, 0, 0);

        var numRunners = 5;
        var numInfected = 2;

        var total  = numRunners + numInfected;

        Supplier<Coordinate> generateRandomPosition =
                () -> new Coordinate(RandomHelper.doubleWithin(1, 511), RandomHelper.doubleWithin(1, 511));

        for (var idx = 0; idx < total; ++idx) {
            this.layers.getChildren().add(new Canvas(512, 512));
        }

        /* Instantiate runners and controllers */
        for (int i = 0; i < numRunners; ++i) {
            try {
                var runner = new Runner(bounds, generateRandomPosition.get(), 0.0002, 0.0,
                        512 * 0.2, 0.0, SimulatedAgent.invalidateByCount(1.0));

                this.manager.registerAgent(runner);
            } catch (Exception e) {
                System.out.printf("Launcher#doTests while trying to instantiate runners%n");
            }
        }

        /* Instantiate infected and controllers */
        for (int i = 0; i < numInfected; ++i) {
            try {
                var infected = new Infected(bounds, generateRandomPosition.get(), 0.000205, 0.0,
                        512 * 0.2, 0.0, SimulatedAgent.invalidateByCount(1.0));

                this.manager.registerAgent(infected);
            } catch (Exception e) {
                System.out.printf("Launcher#doTests while trying to instantiate infected%n");
            }
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

                        var agent = manager.getFigures().get(idx - 1).getAgent();
                        double x = agent.getCoordinate().getX(), y = agent.getCoordinate().getY();

                        gc.save();

                        var r = new Rotate(agent.getAngle(), x, y);
                        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                        gc.drawImage(frameDispatcher.getFrameFor(agent.getUuid()), x - 16, y - 16);

                        gc.restore();
                    }
                });
            }
        }, 0, 30);

        /* Dispatch controllers start */
        try {
            this.manager.startAll();
        }
        catch (Exception e) {
            System.out.printf("Launcher#doTests while trying to dispatch controller start%n");
        }
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
                System.out.printf("Launcher#start where tests failed {%s}%n", e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
