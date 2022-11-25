package in.kplogics.geofence.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import in.kplogics.geofence.base.AppController;

public class PermissionUtil {
    private static final String TAG = "PermissionUtil";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String[] PERMISSIONS_BACKGROUND_LOCATION = new String[]{
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    };

    /**
     * @return Fetches string array for manifest permission
     */
    public static String[] getPermissionList() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        //android 10 accepts direct request
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        return permissions.toArray(new String[0]);
    }

    /**
     * Check if the app already has the permissions
     *
     * @param permissionList permission array
     * @return true or false
     */
    public static boolean hasPermissions(String[] permissionList) {
        for (String permission : permissionList) {
            if (ActivityCompat.checkSelfPermission(AppController.getInstance(), permission) == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "hasPermissions NOT GRANTED: " + permission);
                return false;
            }
        }
        return true;
    }
}
