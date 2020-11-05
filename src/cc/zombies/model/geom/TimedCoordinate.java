package cc.zombies.model.geom;

public class TimedCoordinate {
    private Coordinate coordinate;
    private String description;
    private long epoch;

    public TimedCoordinate(Coordinate coordinate, String description, long epoch) {
        this.setCoordinate(coordinate);
        this.setDescription(description);
        this.setEpoch(epoch);
    }

    public TimedCoordinate(Coordinate coordinate, String description) {
        this.setCoordinate(coordinate);
        this.setDescription(description);
        this.setEpoch(System.currentTimeMillis());
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    @Override
    public String toString() {
        return String.format("TimedCoordinate{coordinate:%s, description:%s, epoch:%d}",
                this.getCoordinate(), this.getDescription(), this.getEpoch());
    }
}