package edu.pdx.cs410J.laurente.client;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.laurente.client.Flight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


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
  public Airline() {
    this.name = null;
    this.flights = null;
  }

  /**
   * Constructor with only the airline's name as the parameter
   * @param name    The airline's name
   */
  public Airline(String name) {
    this.name = name;
    this.flights = new ArrayList<Flight>();
  }

  /**
   * The constructor with the airline's name and a single Flight object as the parameters. The <code>flights</code>
   * field is initalized as an ArrayList of Flight objects and <code>flight</code> object is added to the list.
   * @param name      The airline's name
   * @param flight    The flight of the airline to be added to the airline's collection of flights
   */
  public Airline(String name, AbstractFlight flight) {
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
   * This method adds a flight to the current airline's collection of flights and uses Collections.sort() method to
   * sort the list of Flight utilizing the Flight class overridden compareTo() method in the sorting. Duplicate flights
   * are not added to the list.
   * @param abstractFlight    The flight to be added to the collection of flights
   */
  @Override
  public void addFlight(AbstractFlight abstractFlight) {
    Flight newFlight = ((Flight) abstractFlight);
    if (!this.flights.contains(newFlight)) {
      this.flights.add(newFlight);
      Collections.sort(((ArrayList) this.flights));
    } //else: abstractFlight already exists in the flights list and must not be duplicated
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
