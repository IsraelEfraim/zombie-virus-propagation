package cc.zombies.model.geom;

public class TimedCoordinate {
    private Coordinate coordinate;
    private long epoch;

    public TimedCoordinate(Coordinate coordinate, long epoch) {
        this.setCoordinate(coordinate);
        this.setEpoch(epoch);
    }

    public TimedCoordinate(Coordinate coordinate) {
        this.setCoordinate(coordinate);
        this.setEpoch(System.currentTimeMillis());
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    @Override
    public String toString() {
        return String.format("TimedCoordinate{coordinate:%s, epoch:%d}", this.getCoordinate(), this.getEpoch());
    }
}