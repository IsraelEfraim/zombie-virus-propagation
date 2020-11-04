package cc.zombies.control;

/* Java imports */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JavaFX imports */
import javafx.scene.image.Image;

public class FigureFrameController {
    private int frameSwitchCount;
    private List<Image> frames;
    private Map<String, Integer> framesIndex;
    private Map<String, Integer> counters;

    public FigureFrameController(int frameSwitchCount, List<Image> frames) {
        this.frameSwitchCount = frameSwitchCount;
        this.frames = frames;
        this.framesIndex = new HashMap<>();
        this.counters = new HashMap<>();
    }

    public Image getFrameFor(String key) {
        int frameIndex = 0;
        int counter = 0;
        if (this.framesIndex.containsKey(key)) {
            frameIndex = this.framesIndex.get(key);
            counter = this.counters.get(key);
        }
        else {
            this.framesIndex.put(key, 0);
            this.counters.put(key, 0);
        }

        /* Check frame index increment */
        if (++counter % this.frameSwitchCount == 0) {
            this.framesIndex.put(key, (this.frames.size() + frameIndex + 1) % this.frames.size());
        }
        this.counters.put(key, counter);

        return this.frames.get(frameIndex);
    }
}
