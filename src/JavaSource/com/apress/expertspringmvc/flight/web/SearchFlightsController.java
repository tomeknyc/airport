package JavaSource.com.apress.expertspringmvc.flight.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import JavaSource.com.apress.expertspringmvc.flight.domain.FlightSearchCriteria;
import JavaSource.com.apress.expertspringmvc.flight.service.FlightService;

public class SearchFlightsController extends SimpleFormController {

    private FlightService flights;
    
    public SearchFlightsController() {
        setCommandName("flightSearchCriteria");
        setCommandClass(FlightSearchCriteria.class);
        setFormView("beginSearch");
        setSuccessView("listFlights");
    }

    public void setFlightService(FlightService flights) {
        this.flights = flights;
    }

    @Override
    protected void initBinder(HttpServletRequest req,
            ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                new SimpleDateFormat("yyyy-MM-dd HH"), true));
    }

    @Override
    protected ModelAndView onSubmit(Object command) throws Exception {
        FlightSearchCriteria search = (FlightSearchCriteria) command;
        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("flights", flights.findFlights(search));
        mav.addObject("flightSearchCriteria", search);
        return mav;
    }
    
}
