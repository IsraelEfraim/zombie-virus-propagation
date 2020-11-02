package cc.zombies.model.random;

/* Java imports */
import java.util.concurrent.ThreadLocalRandom;

public class RandomHelper {
    public static double doubleWithin(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}
