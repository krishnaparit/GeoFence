package in.kplogics.geofence.base.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import in.kplogics.geofence.base.AppController;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceive";
    public static final String ACTION_GEOFENCE_EVENT = "in.kplogics.geofence.ACTION_GEOFENCE_EVENT";
    private static FenceEvent fenceEventListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(ACTION_GEOFENCE_EVENT)) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent == null) {
                Log.e(TAG, "onReceive: geoFenceEvent NULL");
                return;
            }
            if (geofencingEvent.hasError()) {
                String errorMessage = GeofenceStatusCodes
                        .getStatusCodeString(geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

                Log.e(TAG, "onReceive: " + geofenceTransition + " > " + AppController.getGson().toJson(triggeringGeofences));

                if (fenceEventListener != null) {
                    fenceEventListener.onFenceUpdate(geofenceTransition, triggeringGeofences);
                }
            } else {
                Log.e(TAG, "onReceive error: " + geofenceTransition);
            }
        }
    }

    public interface FenceEvent {
        void onFenceUpdate(int eventType, List<Geofence> geofences);
    }

    public static void setOnFenceEventListener(FenceEvent eventListener) {
        GeofenceBroadcastReceiver.fenceEventListener = eventListener;
    }
}
