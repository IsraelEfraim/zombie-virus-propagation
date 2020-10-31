package cc.zombies;

/* CC imports */
import cc.zombies.model.agents.MainContainer;
import cc.zombies.model.agents.SimulationContainer;

/* Java imports */

/* JavaFX imports */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* Jade */

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainContainer.start();

        SimulationContainer container = new SimulationContainer("zombie-virus-propagation");

        Parent root = FXMLLoader.load(getClass().getResource("view/ApplicationScene.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 640, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
