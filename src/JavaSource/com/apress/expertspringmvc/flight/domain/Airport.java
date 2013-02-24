package JavaSource.com.apress.expertspringmvc.flight.domain;

public class Airport {

    private String name;
    private String airportCode;
    
    public Airport(String name, String airportCode) {
        this.name = name;
        this.airportCode = airportCode;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
        return name + " (" + airportCode + ")";
    }
}
