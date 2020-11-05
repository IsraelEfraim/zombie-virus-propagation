package cc.zombies.model.behaviours.base;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;

/* Java imports */
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class SelectiveBehaviour extends SimulationBehaviour {
    private final List<Entry<Supplier<Boolean>, SimulationBehaviour>> behaviours;

    @SafeVarargs
    public SelectiveBehaviour(SimulatedAgent agent, Entry<Supplier<Boolean>, SimulationBehaviour>... dispatch) {
        super(agent);
        this.behaviours = List.of(dispatch);
    }

    @Override
    public void action() {
        for (var behaviour : this.behaviours) {
            if (behaviour.getKey().get()) {
                behaviour.getValue().action();
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
