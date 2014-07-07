package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

import java.util.ArrayList;
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
    this.flights = new ArrayList<Flight>();
  }

  Airline(String name, Flight flight) {
    this.name = name;
    this.flights = new ArrayList<Flight>();
    this.addFlight(flight);
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

  @Override
  public String toString() {
    return this.flights.size() > 1?
        this.getName() + " with " + this.getFlights().size() +
        " flights"
      :
        this.getName() + " with " + this.getFlights().size() +
        " flight";
  }
}
