package cc.zombies;

/* CC imports */
import cc.zombies.control.FigureCanvasController;
import cc.zombies.control.FigureFrameController;
import cc.zombies.control.FigureFrameDispatcher;
import cc.zombies.model.agents.MainContainer;
import cc.zombies.model.agents.SimulationManager;
import cc.zombies.model.agents.figures.Infected;
import cc.zombies.model.agents.figures.Runner;
import cc.zombies.model.agents.figures.Warrior;
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
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Launcher extends Application {
    /* JavaFX attributes */

    private Parent root;
    private MediaPlayer player;

    @FXML
    private StackPane layers;

    @FXML
    private ProgressIndicator indStartup;

    @FXML
    private JFXButton btnStart;

    @FXML
    private Node cntPlaceholder;

    /* Jade attributes */

    SimulationManager manager;

    private FigureFrameDispatcher frameDispatcher;
    private FigureCanvasController canvasController;

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

        primaryStage.getIcons().add(new Image(getClass().getResource("/cc/zombies/view/assets/img/scheme/icon.png").toString()));

        var theme = new Media(getClass().getResource("/cc/zombies/view/assets/audio/theme.mp3").toString());
        player = new MediaPlayer(theme);
        player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        player.setVolume(0.5);
        player.play();

        this.btnStart.setOnAction((e) -> Platform.runLater(() -> {
            var fade = new FadeTransition(Duration.seconds(1.0), this.cntPlaceholder);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.play();

            this.startDrawing();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        layers.getChildren().removeIf(
                                (node) -> node.getId() != null && node.getId().equals("cntPlaceholder"));
                        startSimulation();
                    });
                }
            }, 1000);
        }));

        primaryStage.setTitle("Dying Agents");
        primaryStage.setScene(new Scene(root, 512, 512));
        primaryStage.getScene().setOnMouseClicked(
                (e) -> {
                    SimulatedAgent agent = null;
                    if (e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
                        agent = this.manager.getSpawnRunner().get();
                    }
                    else if (!e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        agent = this.manager.getSpawnWarrior().get();
                    }
                    else if (!e.isControlDown() && !e.isAltDown() && e.isShiftDown()) {
                        agent = this.manager.getSpawnInfected().get();
                    }
                    if (agent != null) {
                        agent.setCoordinate(new Coordinate(e.getSceneX(), e.getSceneY()));
                        try {
                            manager.registerAndStartAgent(agent);
                        }
                        catch (Exception ex) {
                            System.out.printf("Launcher#setupUi where couldn't register and start agent%n");
                        }
                    }
                });
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
        this.canvasController = new FigureCanvasController(this.layers, 512, 512);

        this.frameDispatcher.addDispatcher(
                "Runner",
                new FigureFrameController(7, IntStream.range(0, 4).mapToObj(
                    (idx) -> new Image(String.format("/cc/zombies/view/assets/img/figures/runner/mv-ext-%d-90r.png", idx))
                ).collect(Collectors.toList()))
        );

        this.frameDispatcher.addDispatcher(
                "Warrior",
                new FigureFrameController(7, IntStream.range(0, 5).mapToObj(
                        (idx) -> new Image(String.format("/cc/zombies/view/assets/img/figures/warrior/mv-f-%d-90r.png", idx))
                ).collect(Collectors.toList()))
        );

        this.frameDispatcher.addDispatcher(
                "Infected",
                new FigureFrameController(7, IntStream.range(0, 4).mapToObj(
                    (idx) -> new Image(String.format("/cc/zombies/view/assets/img/figures/infected/mv-%d-90r.png", idx))
                ).collect(Collectors.toList()))
        );
    }

    private void drawRotatedImage(GraphicsContext gc, Image image, double x, double y, double angle) {
        gc.save();

        var r = new Rotate(angle, x, y);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(image, x - image.getWidth() / 2, y - image.getHeight() / 2);

        gc.restore();
    }

    private void startDrawing() {
        /* Start UI update background thread */
        var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    synchronized (manager.getFigures()) {
                        for (var figure : manager.getFigures()) {
                            var agent = figure.getAgent();
                            var gc = canvasController.getGraphics2DFor(agent.getUuid());
                            gc.clearRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getWidth());

                            if (!agent.isDead()) {
                                double x = agent.getCoordinate().getX(), y = agent.getCoordinate().getY();
                                drawRotatedImage(gc, frameDispatcher.getFrameFor(agent.getUuid()), x, y, agent.getAngle());
                            }
                        }
                    }
                });
            }
        }, 0, 33);
    }

    private void startSimulation() {
        var bounds = Polygon.from(0, 0, 512, 0, 512, 512, 0, 512, 0, 0);

        var numRunner = 4;
        var numInfected = 3;
        var numWarrior = 2;

        Supplier<Coordinate> generateRandomPosition =
                () -> new Coordinate(RandomHelper.doubleWithin(1, 511), RandomHelper.doubleWithin(1, 511));

        this.manager.setSpawnRunner(() -> new Runner(bounds, generateRandomPosition.get(), 0.0003, 0.0,
                512 * 0.2, 0.0));

        this.manager.setSpawnWarrior(() -> new Warrior(bounds, generateRandomPosition.get(), 0.0003, 0.0,
                512 * 0.2, 512 * 0.025, SimulatedAgent.countCooldown(1.0),
                SimulatedAgent.countCooldown(1.0), SimulatedAgent.countCooldown(1.0)));

        this.manager.setSpawnInfected(() -> new Infected(bounds, generateRandomPosition.get(), 0.0003, 0.0,
                512 * 0.1, 512 * 0.0125));

        /* Instantiate infected and controllers */
        for (int i = 0; i < numWarrior; ++i) {
            try {
                var agent = this.manager.getSpawnWarrior().get();
                this.manager.registerAgent(agent);
            } catch (Exception e) {
                System.out.printf("Launcher#doTests while trying to instantiate infected%n");
            }
        }

        /* Instantiate runners and controllers */
        for (int i = 0; i < numRunner; ++i) {
            try {
                var agent = this.manager.getSpawnRunner().get();
                this.manager.registerAgent(agent);
            } catch (Exception e) {
                System.out.printf("Launcher#doTests while trying to instantiate runners%n");
            }
        }

        /* Instantiate infected and controllers */
        for (int i = 0; i < numInfected; ++i) {
            try {
                var agent = manager.getSpawnInfected().get();
                this.manager.registerAgent(agent);
            } catch (Exception e) {
                System.out.printf("Launcher#doTests while trying to instantiate infected%n");
            }
        }

        /* Dispatch controllers start */
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    manager.startAll();
                }
                catch (Exception e) {
                    System.out.printf("Launcher#doTests while trying to dispatch controller start%n");
                }
            }
        }, 50);
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
                Platform.runLater(() -> {
                    this.indStartup.setVisible(false);
                    this.btnStart.setVisible(true);
                });
            }
            catch (Exception e) {
                System.out.printf("Launcher#start where simulation failed {%s}%n", e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
