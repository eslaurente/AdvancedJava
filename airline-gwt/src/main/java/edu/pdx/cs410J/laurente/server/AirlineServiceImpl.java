package edu.pdx.cs410J.laurente.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.laurente.client.Airline;
import edu.pdx.cs410J.laurente.client.AirlineService;

/**
 * The server-side implementation of the Airline service
 */
public class AirlineServiceImpl extends RemoteServiceServlet implements AirlineService
{
  private AbstractAirline theAirline = null;

  @Override
  public AbstractAirline addAFlight(String airlineName, AbstractFlight flight) {
    if (this.theAirline == null) {
      this.theAirline = new Airline(airlineName);
    }
    this.theAirline.addFlight(flight);
    return this.theAirline;
  }

  @Override
  public AbstractAirline syncAirline() {
    return this.theAirline;
  }

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