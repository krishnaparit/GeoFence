package in.kplogics.geofence;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.kplogics.geofence.base.broadcast.GeofenceBroadcastReceiver;
import in.kplogics.geofence.data.GeoFenceEntries;
import in.kplogics.geofence.databinding.ActivityMainBinding;
import in.kplogics.geofence.utils.Constants;
import in.kplogics.geofence.utils.LocationUtils;
import in.kplogics.geofence.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final List<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;
    private ActivityMainBinding binding;
    private final StringBuilder stringBuilder = new StringBuilder();

    //enable location service launcher
    private final ActivityResultLauncher<Intent> enableLocLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result
            -> requestEnableLocation());

    //location permission launcher
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts
                    .RequestMultiplePermissions(), result -> {
                boolean fineLocationGranted = Boolean.TRUE.equals(result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false));
                boolean coarseLocationGranted = Boolean.TRUE.equals(result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION, false));
                if (fineLocationGranted || coarseLocationGranted) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        boolean backgroundLocationGranted = Boolean.TRUE.equals(result.getOrDefault(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION, false));
                        if (backgroundLocationGranted)
                            requestLocationPermission();
                        else
                            requestBackgroundLocationPermission();
                    } else {
                        requestEnableLocation();
                    }
                } else {
                    requestLocationPermission();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //step1: check for permissions
        requestLocationPermission();

        //step2: check for background permissions for version greater than Android Q
        //step3: if all permissions are granted enable location service
        //step4: initialize GeoFencing logic
        //step5: listen to updates & update the UI accordinglyw
    }

    /**
     * Check if the location permissions are granted
     * if not, request location permissions
     */
    public void requestLocationPermission() {
        if (PermissionUtil.hasPermissions(PermissionUtil.getPermissionList())) {
            requestEnableLocation();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.location_access)
                .setMessage(R.string.location_access_message)
                .setPositiveButton(R.string.grant, (dialogInterface, i) -> {
                    permissionLauncher.launch(PermissionUtil.getPermissionList());
                })
                .setNegativeButton(R.string.reject, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                }).create().show();
    }

    /**
     * For versions greater than Android Q (API 32)
     * Background location access needs to be manually granted by the user with a disclaimer
     */
    public void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (PermissionUtil.hasPermissions(PermissionUtil.PERMISSIONS_BACKGROUND_LOCATION)) {
                requestEnableLocation();
                return;
            }

            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.bg_location_access)
                    .setMessage(R.string.bg_location_access_message)
                    .setPositiveButton(R.string.grant, (dialogInterface, i) -> {
                        permissionLauncher.launch(PermissionUtil.PERMISSIONS_BACKGROUND_LOCATION);
                    })
                    .setNegativeButton(R.string.reject, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    }).create().show();
        }
    }

    /**
     * Request user to enable location service
     */
    private void requestEnableLocation() {
        //start operations if location is enabled
        if (LocationUtils.isLocationEnabled(this)) {
            initOperations();
        } else {
            //request again if not enabled
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.enable_location)
                    .setMessage(R.string.enable_location_message)
                    .setPositiveButton(R.string.enable, (dialogInterface, i) -> {
                        enableLocLauncher.launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    })
                    .create().show();
        }
    }

    /**
     * Initiate Geofence logic
     */
    @SuppressLint("MissingPermission")
    private void initOperations() {
        //get location updates
        LocationUtils.getLocation((lat, lon) -> {
            binding.tvCurrentLocation.setText(String.format(Locale.getDefault(), "Current Location\n%f, %f", lat, lon));
        });

        //create GeoFencing Request
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(getGeoFences()).build();

        //enable scrolling for textView
        binding.tvFenceEvent.setMovementMethod(new ScrollingMovementMethod());

        //create & add GeoFences to the GeofencingClient
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                .addOnSuccessListener(this, aVoid -> {
                    //set progress max
                    binding.progress.setMax(geofencingRequest.getGeofences().size());
                    //append GeoFence events
                    stringBuilder.append(String.format(Locale.getDefault(), "%d GeoFences Added\n\n", geofencingRequest.getGeofences().size()));
                    binding.tvFenceEvent.setText(stringBuilder);
                })
                .addOnFailureListener(this, e -> {
                    stringBuilder.append("\nGeoFences Failed");
                    binding.tvFenceEvent.setText(stringBuilder);
                });


        List<Geofence> triggeredGeoFences = new ArrayList<>();
        //listen to GeoFence broadcast receiver
        GeofenceBroadcastReceiver.setOnFenceEventListener((eventType, geofences) -> {
            triggeredGeoFences.addAll(geofences);
            stringBuilder.append(String.format("> %s : %s\n", geofences.get(0).getRequestId(), (eventType == Geofence.GEOFENCE_TRANSITION_ENTER) ? "Entered" : "Exited"));
            binding.tvFenceEvent.setText(stringBuilder);

            int progress = triggeredGeoFences.size() > 41 ? geofencingRequest.getGeofences().size() : (triggeredGeoFences.size() / 2);
            binding.progress.setProgressCompat(progress, true);
        });
    }

    /**
     * Initiate PendingIntent to receive events from GeofenceBroadcastReceiver
     *
     * @return PendingIntent
     */
    @SuppressLint("InlinedApi")
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        intent.setAction(GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT);
        geofencePendingIntent = PendingIntent
                .getBroadcast(this,
                        0, intent,
                        PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    /**
     * Generate GeoFences list
     *
     * @return list of GeoFences
     */
    private List<Geofence> getGeoFences() {
        if (geofenceList.isEmpty())
            GeoFenceEntries.geoFences.forEach(entry -> geofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getId())
                    .setCircularRegion(
                            entry.getLatitude(),
                            entry.getLongitude(),
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()));
        return geofenceList;
    }
}