package cc.zombies.model.agents.figures.base;

/* CC imports */
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;

/* Java imports */
import java.util.UUID;

/* JADE imports */
import jade.core.Agent;

public abstract class SimulatedAgent extends Agent {
    private Polygon bounds;
    private Coordinate coordinate;
    private String uuid;
    private double speed;
    private double angle;
    private double awarenessRadius;

    public SimulatedAgent(Polygon bounds, Coordinate coordinate, double speed, double angle, double awarenessRadius) {
        this.setBounds(bounds);
        this.setCoordinate(coordinate);;
        this.setUuid(this.randomUUID());
        this.setSpeed(speed);
        this.setAngle(angle);
        this.setAwarenessRadius(awarenessRadius);
    }

    public SimulatedAgent(Coordinate coordinate) {
        this.setCoordinate(coordinate);
        this.setUuid(this.randomUUID());
    }

    public SimulatedAgent() {
        this.setCoordinate(new Coordinate(0,0));
        this.setUuid(this.randomUUID());
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

    private String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public String getUuid() {
        return this.uuid;
    }

    private void setUuid(String uuid) {
        this.uuid = uuid;
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

}