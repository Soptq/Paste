package soptqs.paste.database;

import org.litepal.crud.DataSupport;

public class ExpressSaves extends DataSupport {
    private int id;
    private String time;
    private int isTracking;

    public int getId() {
        return id;
    }

    public void setId(int mid) {
        this.id = mid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsTracking() {
        return isTracking;
    }

    public void setIsTracking(int isTracking) {
        this.isTracking = isTracking;
    }
}
