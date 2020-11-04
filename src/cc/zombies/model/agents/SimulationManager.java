package cc.zombies.model.agents;

/* CC imports */
import cc.zombies.model.agents.figures.base.SimulatedAgent;
import cc.zombies.model.geom.internal.GeometryCalculator;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.TimedCoordinate;

/* Java imports */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* JADE imports */
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class SimulationManager extends Agent {
    private static final Runtime runtime = Runtime.instance();

    private final String name;
    private final AgentContainer container;
    private final AgentController controller;
    private final ProfileImpl profile;
    private final List<AgentReference<SimulatedAgent>> figures;

    public SimulationManager(String name) throws Exception {
        this.name = name;
        this.profile = new ProfileImpl(new ExtendedProperties(new String[]{ "container-name:".concat(this.name) }));
        this.container = runtime.createAgentContainer(this.profile);
        this.figures = new ArrayList<>();
        this.controller = MainContainer.getInstance().getHandle().acceptNewAgent(
                String.format("%s-ds", this.container.getContainerName()), this);
    }

    public AgentContainer getContainer() {
        return this.container;
    }

    public AgentController getController() { return this.controller; };

    public void registerAgent(SimulatedAgent agent) throws Exception {
        this.figures.add(new AgentReference<>(agent, this.container.acceptNewAgent(agent.getUuid(), agent)));
    }

    public void startAll() throws Exception {
        for (var ref : this.figures) {
            ref.getController().start();
        }
    }

    public List<AgentReference<SimulatedAgent>> getFigures() {
        return this.figures;
    }

    @Override
    protected void setup() {
        this.addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                var message = this.myAgent.receive();

                if (message != null) {
                    if (message.getOntology().equalsIgnoreCase("query-nearby")) {
                        var content = message.getContent();
                        var sender = message.getSender();

                        //System.out.printf("Received from %s%n", sender.getLocalName());

                        try {
                            /* Message pattern is "x y radius" */
                            var args = content.split(" ");
                            var parsed = Arrays.stream(args).mapToDouble(Double::parseDouble).toArray();

                            var senderPosition = new Coordinate(parsed[0], parsed[1]);
                            var radius = parsed[2];

                            var map = new HashMap<String, TimedCoordinate>();

                            for (var figure : figures) {
                                if (GeometryCalculator.isPointInRadius(figure.getAgent().getCoordinate(), senderPosition, radius)
                                        && !figure.getAgent().getLocalName().equals(sender.getLocalName())) {
                                    map.put(figure.getAgent().getUuid(), new TimedCoordinate(figure.getAgent().getCoordinate()));
                                }
                            }

                            var builder = new StringBuilder();
                            map.forEach((uuid, tc) -> {
                                builder.append(
                                        /* Sent pattern is "uuid x y epoch" */
                                        String.format("%s %.8f %.8f %d,", uuid, tc.getCoordinate().getX(),
                                                tc.getCoordinate().getY(), tc.getEpoch())
                                );
                            });

                            var reply = new ACLMessage(ACLMessage.INFORM);
                            reply.addReceiver(sender);
                            reply.setLanguage("English");
                            reply.setOntology("reply:query-nearby");
                            reply.setContent(builder.toString());
                            this.myAgent.send(reply);

                            //System.out.printf("Sent reply to %s with %s%n", sender.getLocalName(), builder.toString());
                        }
                        catch (Exception e) {
                            System.out.printf("SimulationContainer#setup where Agent {%s} sent unformatted " +
                                                "query-nearby message%n", sender.getLocalName());
                        }
                    }
                }
                else {
                    this.block();
                }
            }
        });
    }
}