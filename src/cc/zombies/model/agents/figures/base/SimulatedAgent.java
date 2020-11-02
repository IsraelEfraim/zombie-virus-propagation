package cc.zombies.model.agents.figures.base;

/* CC imports */
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;
import cc.zombies.model.geom.internal.GeometryCalculator;

/* Java imports */
import java.util.UUID;

/* JADE imports */
import jade.core.Agent;

public abstract class SimulatedAgent extends Agent {
    private static String IDENTIFIER_SEPARATOR = "$";

    private String identity;
    private String uuid;
    private Polygon bounds;
    private Coordinate coordinate;
    private double speed;
    private double angle;
    private double awarenessRadius;

    public SimulatedAgent(String identity, Polygon bounds, Coordinate coordinate, double speed, double angle, double awarenessRadius) {
        this.setIdentity(identity);
        this.setUuid(this.randomUUID());
        this.setBounds(bounds);
        this.setCoordinate(coordinate);;
        this.setSpeed(speed);
        this.setAngle(angle);
        this.setAwarenessRadius(awarenessRadius);
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

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAwarenessRadius() {
        return awarenessRadius;
    }

    public void setAwarenessRadius(double awarenessRadius) {
        this.awarenessRadius = awarenessRadius;
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

        /* Move in X axis */
        if (coordinate.getX() > position.getX()) {
            position.setX(position.getX() + this.speed);
        } else if (coordinate.getX() < position.getX()) {
            position.setX(position.getX() - this.speed);
        }

        /* Move in Y axis */
        if (coordinate.getY() > position.getY()) {
            position.setY(position.getY() + this.speed);
        } else if (coordinate.getY() < position.getY()) {
            position.setY(position.getY() - this.speed);
        }

        if (this.canMoveTo(coordinate)) {
            this.coordinate = position;
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
}
