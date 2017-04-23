package androdevians.pilotplus;

/**
 * Created by sanjit on 23/4/17.
 */

public class PlaceOfInterest {
    private float latitude;
    private float longitude;
    private long id;
    private String name;

    public PlaceOfInterest(float latitude, float longitude, long id, String name) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PlaceOfInterest{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
