package ru.android.bluetooth.common.location;

/**
 * Created by yasina on 26.09.17.
 */

public interface LocationView {
    void setLonLat(double lat, double lon);
    boolean setScheduleGeneration();
}