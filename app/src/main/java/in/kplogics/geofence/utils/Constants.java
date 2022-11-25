package in.kplogics.geofence.utils;

import java.util.concurrent.TimeUnit;

public class Constants {

    //Geo fence expiration in millis
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(15);

    //Geo fence radius in meters
    public static final float GEOFENCE_RADIUS_IN_METERS = 100F;
}
