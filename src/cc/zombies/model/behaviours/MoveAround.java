package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.Coordinate;

/* Java imports */
import java.util.ArrayList;
import java.util.List;

/* JADE imports */
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

// @TODO: modificar a classe
public class MoveAround extends Behaviour {
    private List<Coordinate> path = new ArrayList<>();
    private int control;

    public MoveAround(Agent a) {
        super(a);
        SimulatedAgent sa = (SimulatedAgent) a;
        this.path.add(new Coordinate(sa.getCoordinate().getX() + 50, sa.getCoordinate().getY()));
        this.path.add(new Coordinate(sa.getCoordinate().getX() - 50, sa.getCoordinate().getY()));
        this.control = 0;
    }

    @Override
    public void action() {
        SimulatedAgent sa = (SimulatedAgent)myAgent;
        if (this.control == 0) {
            sa.setCoordinate(new Coordinate(sa.getCoordinate().getX() + sa.getSpeed(), sa.getCoordinate().getY()));
            if (sa.getCoordinate().getX() >= this.path.get(0).getX()) {
                this.control = 1;
            }

        } else {
            sa.setCoordinate(new Coordinate(sa.getCoordinate().getX() - sa.getSpeed(), sa.getCoordinate().getY()));
            if (sa.getCoordinate().getX() <= this.path.get(1).getX()) {
                this.control = 0;
            }
        }

        // @TODO Checar se vamos manter essa função
        /*sa.sendPositionUpdate();*/
    }

    @Override
    public boolean done() {
        return false;
    }
}
