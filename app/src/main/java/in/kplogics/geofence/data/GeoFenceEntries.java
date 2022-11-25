package in.kplogics.geofence.data;

import java.util.ArrayList;
import java.util.List;

public class GeoFenceEntries {
    private final String id;
    private final double latitude;
    private final double longitude;

    public static final List<GeoFenceEntries> geoFences = new ArrayList<GeoFenceEntries>() {{
        add(new GeoFenceEntries("fence1", 12.953013054035946, 77.5417514266668));
        add(new GeoFenceEntries("fence2", 12.95428866232216, 77.5438757362066));
        add(new GeoFenceEntries("fence3", 12.95558517552543, 77.54565672299249));
        add(new GeoFenceEntries("fence4", 12.956442543452548, 77.54752354046686));
        add(new GeoFenceEntries("fence5", 12.95675621390793, 77.54919723889215));
        add(new GeoFenceEntries("fence6", 12.957069883968225, 77.55112842938287));
        add(new GeoFenceEntries("fence7", 12.957711349517467, 77.55308710458465));
        add(new GeoFenceEntries("fence8", 12.958464154110917, 77.55514704110809));
        add(new GeoFenceEntries("fence9", 12.959656090062529, 77.5559409749765));
        add(new GeoFenceEntries("fence10", 12.960814324441305, 77.5574167738546));
        add(new GeoFenceEntries("fence11", 12.961253455257907, 77.5592192183126));
        add(new GeoFenceEntries("fence12", 12.96156308861349, 77.56126500049922));
        add(new GeoFenceEntries("fence13", 12.961814019848779, 77.56308890262935));
        add(new GeoFenceEntries("fence14", 12.962775920574138, 77.56592131534907));
        add(new GeoFenceEntries("fence15", 12.963340512746937, 77.5676379291186));
        add(new GeoFenceEntries("fence16", 12.96411114676894, 77.56951526792183));
        add(new GeoFenceEntries("fence17", 12.964382986146292, 77.5717254081501));
        add(new GeoFenceEntries("fence18", 12.964584624077109, 77.57370388966811));
        add(new GeoFenceEntries("fence19", 12.964542802687657, 77.57591402989638));
        add(new GeoFenceEntries("fence20", 12.963795326167373, 77.57798997552734));
        add(new GeoFenceEntries("fence21", 12.96358621848664, 77.58015720041138));
        add(new GeoFenceEntries("fence22", 12.963481664580394, 77.58189527185303));
    }};


    public GeoFenceEntries(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
