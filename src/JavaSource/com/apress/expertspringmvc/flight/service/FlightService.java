package JavaSource.com.apress.expertspringmvc.flight.service;

import java.util.List;

import JavaSource.com.apress.expertspringmvc.flight.domain.Flight;
import JavaSource.com.apress.expertspringmvc.flight.domain.FlightSearchCriteria;
import JavaSource.com.apress.expertspringmvc.flight.domain.SpecialDeal;

public interface FlightService {

    List<SpecialDeal> getSpecialDeals();
    
    List<Flight> findFlights(FlightSearchCriteria search);
    
}
