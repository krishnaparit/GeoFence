package in.kplogics.geofence.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import in.kplogics.geofence.base.AppController;

public class LocationUtils {
    private static final String TAG = "LocationUtils";

    /**
     * This method initializes DeviceInfo with location data
     */
    @SuppressLint("MissingPermission")
    public static void getLocation(LocationEvent event) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(AppController.getInstance());
        requestNewLocationData(fusedLocationClient, event);
    }

    /**
     * Requests new location data if the current location is NULL
     *
     * @param fusedLocationProviderClient requires FusedLocationProviderClient
     */
    @SuppressLint("MissingPermission")
    private static void requestNewLocationData(FusedLocationProviderClient fusedLocationProviderClient, LocationEvent event) {
        Log.e(TAG, "requestNewLocationData: ");
        if (!isLocationEnabled(AppController.getInstance())) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppController.getInstance().startActivity(intent);
            return;
        }

        LocationRequest mLocationRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY,
                0)
                //set 10meter distance update
                .setMinUpdateDistanceMeters(10)
                //set 500millis interval
                .setMinUpdateIntervalMillis(500)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null && event != null)
                    event.onCoordinatesReceived(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.e(TAG, "onLocationAvailability: " + locationAvailability.isLocationAvailable());
            }
        }, Looper.myLooper());
    }

    public interface LocationEvent {
        void onCoordinatesReceived(double lat, double lon);
    }

    /**
     * Check if the location service is enabled
     *
     * @param context Context
     * @return boolean true: enabled & false: disabled
     */
    public static boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

}
