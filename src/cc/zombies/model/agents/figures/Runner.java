package cc.zombies.model.agents.figures;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.behaviours.MoveAround;

// @TODO: modificar a classe
public class Runner extends SimulatedAgent {
    protected void setup() {
        this.setSpeed(0.0005);
        this.addBehaviour(new MoveAround(this));

    }
}
