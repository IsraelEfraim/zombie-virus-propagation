package cc.zombies.control;

/* Java imports */
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

/* JavaFX imports */
import javafx.scene.image.Image;

public class FigureFrameDispatcher {
    private List<Entry<String, FigureFrameController>> frameControllers;
    private Function<String, String> hash;

    public FigureFrameDispatcher(Function<String, String> hash) {
        this.frameControllers = new ArrayList<>();
        this.hash = hash;
    }

    public void addDispatcher(String type, FigureFrameController frameController) {
        this.frameControllers.add(new AbstractMap.SimpleEntry<>(type, frameController));
    }

    public Image getFrameFor(String key) {
        var hash = this.hash.apply(key);
        for (var entry : this.frameControllers) {
            if (entry.getKey().equals(hash)) {
                return entry.getValue().getFrameFor(key);
            }
        }

        throw new IndexOutOfBoundsException(String.format("FigureFrameDispatcher#getFrameFor with " +
                                                            "key that couldn't be translated"));
    }
}
