package JavaSource.com.apress.expertspringmvc.flight.domain;

import java.util.Date;

public class FlightLeg {

    private Airport departFrom;
    private Date departOn;
    private Airport arriveAt;
    private Date arriveOn;
    
    public FlightLeg(Airport departFrom, Date departOn, Airport arriveAt,
            Date arriveOn) {
        this.arriveAt = arriveAt;
        this.arriveOn = arriveOn;
        this.departFrom = departFrom;
        this.departOn = departOn;
    }

    public Airport getArriveAt() {
        return arriveAt;
    }

    public Date getArriveOn() {
        return arriveOn;
    }

    public Date getDepartOn() {
        return departOn;
    }

    public Airport getDepartFrom() {
        return departFrom;
    }
    
}
