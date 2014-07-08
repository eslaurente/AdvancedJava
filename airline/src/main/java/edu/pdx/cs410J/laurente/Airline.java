package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class represents an airline which has a collection of flights. This class extends the AbstractAirline abstract
 * class. It contains information about an airline's name, along with storing and retrieving information about it and its
 * collection of flights.
 */
public class Airline extends AbstractAirline {
  private String name;
  private Collection<Flight> flights;

  /**
   * No-arg constructor
   */
  Airline() {
    this.name = null;
    this.flights = null;
  }

  /**
   * Constructor with only the airline's name as the parameter
   * @param name    The airline's name
   */
  Airline(String name) {
    this.name = name;
    this.flights = new ArrayList<Flight>();
  }

  /**
   * The constructor with the airline's name and a single Flight object as the parameters. The <code>flights</code>
   * field is initalized as an ArrayList of Flight objects and <code>flight</code> object is added to the list.
   * @param name      The airline's name
   * @param flight    The flight of the airline to be added to the airline's collection of flights
   */
  Airline(String name, AbstractFlight flight) {
    this.name = name;
    this.flights = new ArrayList<Flight>();
    if (flight instanceof  Flight) {
      this.addFlight(flight);
    }
  }

  /**
   * This method retrieves the airline's name
   * @return    The airline's name
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * This method adds a flight to the current airline's collection of flights
   * @param abstractFlight    The flight to be added to the collection of flights
   */
  @Override
  public void addFlight(AbstractFlight abstractFlight) {
    if (abstractFlight instanceof Flight) {
        flights.add((Flight) abstractFlight);
    }
  }

  /**
   * This method retrieves this airline's collection of flights
   * @return    This airline's collection of flights
   */
  @Override
  public Collection getFlights() {
    return this.flights;
  }

  /**
   * This method overrides AbstractAirline's toString() method to allow proper printing of the word "flight"'s singular
   * and plural form, depending on if this airline's collection size is more than one or not. This is used to construct
   * the String object that describes the details of this airline and its flights collection size.
   * @return    The String value that describes this airline and how many flights it has
   */
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
