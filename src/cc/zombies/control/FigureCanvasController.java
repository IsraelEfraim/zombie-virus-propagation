package cc.zombies.control;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class FigureCanvasController {
    private final Pane parent;
    private double width;
    private double height;
    private final Map<String, Canvas> references;

    public FigureCanvasController(Pane parent, double width, double height) {
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.references = new HashMap<>();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Canvas getCanvasFor(String key) {
        if (this.references.containsKey(key)) {
            return this.references.get(key);
        }
        else {
            var canvas = new Canvas(this.getWidth(), this.getHeight());

            Platform.runLater(() -> {
                parent.getChildren().add(canvas);
            });

            this.references.put(key, canvas);

            return canvas;
        }
    }

    public GraphicsContext getGraphics2DFor(String key) {
        return this.getCanvasFor(key).getGraphicsContext2D();
    }
}
