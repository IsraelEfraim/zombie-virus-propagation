package cc.zombies.model.agents.figures.base;

/* CC imports */
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;
import cc.zombies.model.geom.TimedCoordinate;
import cc.zombies.model.geom.internal.GeometryCalculator;

/* Java imports */
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/* JADE imports */
import jade.core.Agent;

public abstract class SimulatedAgent extends Agent {
    private static final String IDENTIFIER_SEPARATOR = "$";
    private static final List<String> registered = new ArrayList<>();

    private String identity;
    private String uuid;
    private Polygon bounds;
    private Coordinate coordinate;
    private double speed;
    private double angle;
    private double awarenessRadius;
    private double actionRadius;
    private final Function<SimulatedAgent, Boolean> invalidated;
    private final Map<String, TimedCoordinate> sensed;
    private Map<String, TimedCoordinate> lastSensed;

    public SimulatedAgent(String identity, Polygon bounds, Coordinate coordinate, double speed, double angle,
                            double awarenessRadius, double actionRadius, Function<SimulatedAgent, Boolean> invalidated) {
        this.setIdentity(identity);
        this.setUuid(this.randomUUID());
        this.setBounds(bounds);
        this.setCoordinate(coordinate);
        this.setSpeed(speed);
        this.setAngle(angle);
        this.setAwarenessRadius(awarenessRadius);
        this.setActionRadius(actionRadius);
        this.invalidated = invalidated;
        this.sensed = new HashMap<>();
        this.lastSensed = new HashMap<>();
    }

    private String getIdentity() {
        return this.identity;
    }

    private void setIdentity(String identity) {
        this.identity = identity;
    }

    private String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public String getUuid() {
        return String.format("%s%s%s", this.getIdentity(), SimulatedAgent.IDENTIFIER_SEPARATOR, this.uuid);
    }

    private void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public void setBounds(Polygon bounds) {
        this.bounds = bounds;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        if (this.coordinate != null) {
            this.setAngle(
                    /*
                     * atan2(     delta(y)     ,      delta(x)     )
                     * atan2(next.y - current.y, next.x - current.x)
                     */
                    Math.toDegrees(Math.atan2(coordinate.getY() - this.coordinate.getY(),
                                                coordinate.getX() - this.coordinate.getX()))
            );
        }
        this.coordinate = coordinate;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {  this.angle = angle; }

    public double getAwarenessRadius() {
        return awarenessRadius;
    }

    public void setAwarenessRadius(double awarenessRadius) {
        this.awarenessRadius = awarenessRadius;
    }

    public double getActionRadius() {
        return actionRadius;
    }

    public void setActionRadius(double actionRadius) {
        this.actionRadius = actionRadius;
    }

    public Function<SimulatedAgent, Boolean> getInvalidated() {
        return invalidated;
    }

    public Map<String, TimedCoordinate> getSensed() {
        return sensed;
    }

    public Map<String, TimedCoordinate> getLastSensed() {
        return lastSensed;
    }

    public void setLastSensed(Map<String, TimedCoordinate> lastSensed) {
        this.lastSensed = lastSensed;
    }

    public String getType() {
        return this.getIdentity();
    }

    // @TODO Checar se vamos manter essa função
    /*public void sendPositionUpdate() {
        var message = new ACLMessage(ACLMessage.INFORM);
        var coordinate = this.getCoordinate();

        message.addReceiver(new AID("position-receptor", AID.ISLOCALNAME));
        message.setLanguage("English");
        message.setOntology("position-update");
        message.setContent(String.format("%s %.10f %.10f", this.getUuid(), coordinate.getX(), coordinate.getY()));

        this.send(message);
    }*/

    public boolean canMoveTo(Coordinate coordinate) {
        return this.bounds.contains(coordinate);
    }

    public boolean moveInDirectionOf(Coordinate coordinate) {
        var position = new Coordinate(this.coordinate.getX(), this.coordinate.getY());

        /*
         * Corrected Movement Pattern Algorithm:
         * If (coordinate.axis is bigger than position.axis) then
         *   nextPosition.axis = position.axis + speed
         *     if  (nextPosition.axis run past [target] coordinate.axis) then
         *       nextPosition.axis = coordinate.axis
         * else if (coordinate.axis is lesser than position.axis) then
         *   nextPosition.axis = position.axis - speed
         *     if  (nextPosition.axis run past [target] coordinate.axis) then
         *       nextPosition.axis = coordinate.axis
         *
         * Setting nextPosition.axis to be coordinate.axis when nextPosition.axis
         * would have run past coordinate.axis is necessary so that the agent
         * doesn't keep going back and forth on its discrete walking grid, while
         * the area itself is continuous.
         */

        /* Move in X axis */
        if (coordinate.getX() > position.getX()) {
            var next = position.getX() + this.speed;
            if (next > coordinate.getX()) {
                next = coordinate.getX();
            }
            position.setX(next);
        } else if (coordinate.getX() < position.getX()) {
            var next = position.getX() - this.speed;
            if (next < coordinate.getX()) {
                next = coordinate.getX();
            }
            position.setX(next);
        }

        /* Move in Y axis */
        if (coordinate.getY() > position.getY()) {
            var next = position.getY() + this.speed;
            if (next > coordinate.getY()) {
                next = coordinate.getY();
            }
            position.setY(next);
        } else if (coordinate.getY() < position.getY()) {
            var next = position.getY() - this.speed;
            if (next < coordinate.getY()) {
                next = coordinate.getY();
            }
            position.setY(next);
        }

        if (this.canMoveTo(coordinate)) {
            this.setCoordinate(position);
            return true;
        } else {
            return false;
        }
    }

    public boolean reached(Coordinate coordinate, double radius) {
        return GeometryCalculator.isPointInRadius(this.coordinate, coordinate, radius);
    }

    public static String getTypeByUuid(String uuid) {
        var idx = uuid.indexOf(SimulatedAgent.IDENTIFIER_SEPARATOR);

        if (idx > 0) {
            return uuid.substring(0, idx);
        }
        else {
            throw new RuntimeException("SimulatedAgent#getTypeByUuid with unformatted string");
        }
    }

    public static Function<SimulatedAgent, Boolean> invalidateByCount(Double limit) {
        var n = new AtomicReference<>(0.0);

        return (agent) -> {
            var result = n.updateAndGet(v -> v + agent.getSpeed());
            if (result >= limit) {
                n.set(0.0);
                return true;
            }
            return false;
        };
    }

    private void register(String identity) {
        SimulatedAgent.registered.add(identity);
    }
}
