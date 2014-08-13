package edu.pdx.cs410J.laurente.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

/**
 * This interface is a GWT remote service that performs functions on the server-side AbstractAirline airline object
 */
@RemoteServiceRelativePath("flight")
public interface AirlineService extends RemoteService {
  /**
   * This is the server-side abstract method that will add an AbstractFlight object to the server
   * @param airlineName     The airline's name to be added, if the airline needs to be newly-instantiated
   * @param flight          The AbstractFlight object flight information to be added to the airline
   * @return                The AbstractAirline airline object on the server's side
   */
  public AbstractAirline addAFlight(String airlineName, AbstractFlight flight);

  /**
   * This the server-side abstract method returns the server-side's current state of the airline object
   * @return    The AbstractAirline server-side airline object
   */
  public AbstractAirline syncAirline();

  /**
   * This the server-side abstract method deletes an AbstractFlight flight information from the server-side airline object
   * @param flight          The flight to be deleted
   * @return                The removal process. Null if the flight object does is not contained in the server-side airline's
   *                        list of flights
   */
  public boolean deleteFlight(AbstractFlight flight);
}
