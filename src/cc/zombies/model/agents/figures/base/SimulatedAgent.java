package cc.zombies.model.agents.figures.base;

/* CC imports */
import cc.zombies.model.agents.util.Cooldown;
import cc.zombies.model.geom.Coordinate;
import cc.zombies.model.geom.Polygon;
import cc.zombies.model.geom.TimedCoordinate;
import cc.zombies.model.geom.internal.GeometryCalculator;

/* Java imports */
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/* JADE imports */
import jade.core.Agent;

public abstract class SimulatedAgent extends Agent {
    public static abstract class Status {
        public static final String LIVE = "live";
        public static final String DEAD = "dead";
        public static final String MISSING = "missing";
    }

    private static final String IDENTIFIER_SEPARATOR = "$";

    public static double DEFAULT_SENSE_COOLDOWN = 1.0;
    public static double DEFAULT_COMMS_COOLDOWN = 1.0;
    public static double DEFAULT_SKILL_COOLDOWN = 3.0;

    private String identity;
    private String uuid;
    private String status;
    private Polygon bounds;
    private Coordinate coordinate;
    private double speed;
    private double angle;
    private double awarenessRadius;
    private double actionRadius;
    private final Cooldown senseCooldown;
    private final Cooldown commsCooldown;
    private final Cooldown skillCooldown;
    private final Map<String, TimedCoordinate> sensed;
    private Map<String, TimedCoordinate> lastSensed;

    public SimulatedAgent(String identity, String status, Polygon bounds, Coordinate coordinate, double speed,
                          double angle, double awarenessRadius, double actionRadius, Cooldown senseCooldown,
                          Cooldown commsCooldown, Cooldown skillCooldown) {
        this.setIdentity(identity);
        this.setUuid(this.randomUUID());
        this.setStatus(status);
        this.setBounds(bounds);
        this.setCoordinate(coordinate);
        this.setSpeed(speed);
        this.setAngle(angle);
        this.setAwarenessRadius(awarenessRadius);
        this.setActionRadius(actionRadius);
        this.senseCooldown = senseCooldown;
        this.commsCooldown = commsCooldown;
        this.skillCooldown = skillCooldown;
        this.sensed = new HashMap<>();
        this.lastSensed = new HashMap<>();
    }

    public SimulatedAgent(String identity, Polygon bounds, Coordinate coordinate, double speed, double angle,
                          double awarenessRadius, double actionRadius, Cooldown senseCooldown,
                          Cooldown commsCooldown, Cooldown skillCooldown) {
        this.setIdentity(identity);
        this.setUuid(this.randomUUID());
        this.setStatus(Status.LIVE);
        this.setBounds(bounds);
        this.setCoordinate(coordinate);
        this.setSpeed(speed);
        this.setAngle(angle);
        this.setAwarenessRadius(awarenessRadius);
        this.setActionRadius(actionRadius);
        this.senseCooldown = senseCooldown;
        this.commsCooldown = commsCooldown;
        this.skillCooldown = skillCooldown;
        this.sensed = new HashMap<>();
        this.lastSensed = new HashMap<>();
    }

    public SimulatedAgent(String identity, Polygon bounds, Coordinate coordinate, double speed, double angle,
                            double awarenessRadius, double actionRadius) {
        this.setIdentity(identity);
        this.setUuid(this.randomUUID());
        this.setStatus(Status.LIVE);
        this.setBounds(bounds);
        this.setCoordinate(coordinate);
        this.setSpeed(speed);
        this.setAngle(angle);
        this.setAwarenessRadius(awarenessRadius);
        this.setActionRadius(actionRadius);
        this.senseCooldown = SimulatedAgent.countCooldown(SimulatedAgent.DEFAULT_SENSE_COOLDOWN);
        this.commsCooldown = SimulatedAgent.countCooldown(SimulatedAgent.DEFAULT_COMMS_COOLDOWN);
        this.skillCooldown = SimulatedAgent.countCooldown(SimulatedAgent.DEFAULT_SKILL_COOLDOWN);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                    /* atan2(delta(y), delta(x)) */
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

    public Cooldown getSenseCooldown() {
        return senseCooldown;
    }

    public Cooldown getCommsCooldown() { return commsCooldown; }

    public Cooldown getSkillCooldown() { return skillCooldown; }

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

    public boolean isDead() {
        return this.getStatus().equalsIgnoreCase(Status.DEAD);
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

    public void moveInDirectionOf(Coordinate coordinate) {
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

    public static Cooldown countCooldown(Double limit) {
        var timing = new AtomicReference<>(0.0);
        var coolingDown = new AtomicReference<>(false);

        return new Cooldown() {
            @Override
            public boolean check(SimulatedAgent agent) {
                var available = !coolingDown.get();

                if (!available) {
                    var result = timing.updateAndGet(v -> v + agent.getSpeed());
                    if (result >= limit) {
                        timing.set(0.0);
                        coolingDown.set(false);
                        available = true;
                    }
                }

                return available;
            }

            @Override
            public void use() {
                coolingDown.compareAndSet(false, true);
            }
        };
    }
}
