package in.kplogics.geofence.base;

import android.app.Application;

import com.google.gson.Gson;

public class AppController extends Application {
    private static AppController instance;
    private static Gson gsonInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * Get a synchronized instance of the app
     *
     * @return Application instance
     */
    public static synchronized AppController getInstance() {
        if (instance == null)
            instance = new AppController();
        return instance;
    }

    /**
     * Get a synchronized instance of the Gson object
     *
     * @return Gson instance
     */
    public static synchronized Gson getGson() {
        if (gsonInstance == null)
            gsonInstance = new Gson();
        return gsonInstance;
    }
}
