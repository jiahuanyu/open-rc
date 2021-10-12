package me.jiahuan.openrc.device.background.device;

import me.jiahuan.openrc.device.background.model.Point;

public class Pointer {

    private final long id;

    private final int localId;

    private Point point;
    private float pressure;
    private boolean up;

    public Pointer(long id, int localId) {
        this.id = id;
        this.localId = localId;
    }

    public long getId() {
        return id;
    }

    public int getLocalId() {
        return localId;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }
}
