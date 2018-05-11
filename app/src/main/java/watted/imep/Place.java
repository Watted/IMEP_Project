package watted.imep;

/**
 * Model class for Place (json example).
 */
public class Place {
    private final String name;
    private final String vicinity;
    private final String icone;
    private final String lng,lat;
    private final double distance;


    public Place(String name, String vicinity, String icone,String lat,String lng,double dis) {
        this.name = name;
        this.vicinity = vicinity;
        this.icone = icone;
        this.lat = lat;
        this.lng = lng;
        this.distance=dis;

    }


    public String getname() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public double getDistance() {
        return distance;
    }

    public String getIcone() {
        return icone;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    @Override

    public String toString() {
        return "nearest{" +
                "name='" + name + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", icone='" + icone + '\'' +
                '}';
    }
}