package cc.zombies.model.agents;

/* CC imports */
import cc.zombies.model.geom.Coordinate;

/* Java imports */
import java.util.function.BiConsumer;

/* JADE imports */
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class PositionReceptor extends Agent {
    private BiConsumer<String, Coordinate> callback;

    public PositionReceptor(BiConsumer<String, Coordinate> callback) {
        this.setCallback(callback);
    }

    private void setCallback(BiConsumer<String, Coordinate> callback) {
        this.callback = callback;
    }

    @Override
    protected void setup() {
        this.addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                var message = myAgent.receive();
                if (message != null) {
                    var content = message.getContent();
                    var args = content.split(" ");

                    try {
                        callback.accept(args[0], Coordinate.from(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
                    }
                    catch (Exception e) {
                        System.out.println(String.format("Agent %s sent invalid format message [%].",
                                                message.getSender().getName(), message.getContent()));
                    }
                }
                else {
                    this.block();
                }
            }
        });
    }

    /*
    @TODO Deletar se encontrar solução melhor

    Como utilizar

    var map = new HashMap<String, Coordinate>();

    var receptor = new PositionReceptor((uuid, coordinate) -> {
        map.put(uuid, coordinate);

        Platform.runLater(() -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.BLACK);

            map.forEach((mapUuid, mapCoordinate) -> {
                gc.fillOval(mapCoordinate.getX(), mapCoordinate.getY(), 512 * 0.2, 512 * 0.2);
            });
        });
    });

    var receptorController = container.getAgentContainer().acceptNewAgent("position-receptor", receptor);
    receptorController.start();
    */
}
