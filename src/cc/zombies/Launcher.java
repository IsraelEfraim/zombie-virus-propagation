package cc.zombies;

/* CC imports */
import cc.zombies.model.agents.MainContainer;
import cc.zombies.model.agents.SimulationContainer;
import cc.zombies.model.agents.figures.Runner;
import cc.zombies.model.geom.Coordinate;

/* Java imports */
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/* JADE imports */
import jade.wrapper.AgentController;

public class Launcher extends Application {
    private Parent root;
    
    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainContainer.start();

        SimulationContainer container = new SimulationContainer("zombie-virus-propagation");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ApplicationScene.fxml"));
        loader.setController(this);
        this.root = loader.load();

        primaryStage.setTitle("Zombie Virus Propagation");
        primaryStage.setScene(new Scene(root, 512, 512));
        primaryStage.show();

        this.gc = canvas.getGraphicsContext2D();

        // @TODO: remover todo o teste
        Runner runner = new Runner();
        runner.setCoordinate(new Coordinate(256, 256));
        AgentController ac = container.getContainer().acceptNewAgent("My runner", runner);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Platform.runLater(() -> {
                    Coordinate coordinate = runner.getCoordinate();
                    gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
                    gc.setFill(Color.BLUE);
                    gc.fillOval(coordinate.getX(), coordinate.getY(), 10 ,10);
                });
            }
        }, 0, 10);

        container.getContainer().start();
        ac.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
