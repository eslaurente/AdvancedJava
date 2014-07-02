package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

import java.util.Collection;

/**
 * Created by emerald on 7/2/14.
 */
public class Airline extends AbstractAirline {
    private String name;
    private Collection<Flight> flights;

    Airline() {
        this.name = null;
        this.flights = null;
    }

    Airline(String name) {
        this.name = name;
        this.flights = null;
    }

    Airline(String name, Collection<Flight> flights) {
        this.name = name;
        this.flights = flights;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void addFlight(AbstractFlight abstractFlight) {
        if (abstractFlight instanceof Flight) {
            flights.add((Flight) abstractFlight);
        }
    }

    @Override
    public Collection getFlights() {
        return this.flights;
    }
}
