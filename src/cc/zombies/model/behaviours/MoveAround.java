package cc.zombies.model.behaviours;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.random.RandomHelper;

/* Java imports */
import java.util.LinkedList;
import java.util.List;

/* JADE imports */
import jade.core.behaviours.Behaviour;

// @TODO Código operante, mover para uma classe com comportamento condicional
public class MoveAround extends Behaviour {
    private final SimulatedAgent agent;
    private final List<Coordinate> path;
    private final double[] kernel;

    public MoveAround(SimulatedAgent a) {
        super(a);
        this.agent = (SimulatedAgent) myAgent;
        this.path = new LinkedList<>();
        this.kernel = new double[] { 0.319466, 0.361069, 0.319466 };
    }

    @Override
    public void action() {
        if (this.path.isEmpty()) {
            this.generateRandomPath();
        }

        var next = this.path.stream().findFirst();
        if (next.isPresent()) {
            this.agent.moveInDirectionOf(next.get());

            if (this.agent.reached(next.get(), this.agent.getSpeed())) {
                this.path.remove(0);
            }
        }
        else {
            throw new RuntimeException(
                    String.format("MoveAround#action where %s had no pathing", this.agent.getUuid())
            );
        }

    }

    @Override
    public boolean done() {
        return false;
    }

    protected void generateRandomPath() {
        if (this.path.isEmpty()) {
            var position = this.agent.getCoordinate();
            // @TODO Refatorar para que a quantidade de passos seja configurável
            var count = 3;

            /* Generate path points */
            for (var idx = 0; idx < count; ++idx) {
                var newPosition = new Coordinate(0, 0);
                do {
                    newPosition.setX(
                            RandomHelper.doubleWithin(position.getX() - this.agent.getAwarenessRadius(),
                                    position.getX() + this.agent.getAwarenessRadius())
                    );

                    newPosition.setY(
                            RandomHelper.doubleWithin(position.getY() - this.agent.getAwarenessRadius(),
                                    position.getY() + this.agent.getAwarenessRadius())
                    );
                } while (!this.agent.canMoveTo(newPosition));
                this.path.add(newPosition);

                position = newPosition;
            }

            // @TODO Remover responsabilidade de suavizar desta classe

            /* Smooth point in the middle */
            this.path.get(1).setX(
                this.path.get(0).getX() * this.kernel[0] +
                this.path.get(1).getX() * this.kernel[1] +
                this.path.get(2).getX() * this.kernel[2]
            );

            this.path.get(1).setY(
                    this.path.get(0).getY() * this.kernel[0] +
                    this.path.get(1).getY() * this.kernel[1] +
                    this.path.get(2).getY() * this.kernel[2]
            );
        }
    }
}