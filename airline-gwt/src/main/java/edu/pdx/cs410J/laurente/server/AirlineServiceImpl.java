package edu.pdx.cs410J.laurente.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.laurente.client.Airline;
import edu.pdx.cs410J.laurente.client.AirlineService;

/**
 * This class implements the server-side abstract methods from the AirlineService interface
 */
public class AirlineServiceImpl extends RemoteServiceServlet implements AirlineService
{
  private AbstractAirline theAirline = null; //the server-side working airline object

  /**
   * This is the server-side method that will add an AbstractFlight object to the server
   * @param airlineName     The airline's name to be added, if the airline needs to be newly-instantiated
   * @param flight          The AbstractFlight object flight information to be added to the airline
   * @return                The AbstractAirline airline object on the server's side
   */
  @Override
  public AbstractAirline addAFlight(String airlineName, AbstractFlight flight) {
    if (this.theAirline == null) {
      this.theAirline = new Airline(airlineName);
    }
    this.theAirline.addFlight(flight);
    return this.theAirline;
  }

  /**
   * This the server-side method returns the server-side's current state of the airline object
   * @return    The AbstractAirline server-side airline object
   */
  @Override
  public AbstractAirline syncAirline() {
    return this.theAirline;
  }

  /**
   * This the server-side method deletes an AbstractFlight flight information from the server-side airline object
   * @param flight          The flight to be deleted
   * @return                The removal process. Null if the flight object does is not contained in the server-side airline's
   *                        list of flights
   */
  @Override
  public boolean deleteFlight(AbstractFlight flight) {
    if (this.theAirline == null) {
      return false;
    }
    boolean result = this.theAirline.getFlights().remove(flight);
    if (result == true && this.theAirline.getFlights().size() == 0) {
      this.theAirline = null;
    }
    return result;
  }
}