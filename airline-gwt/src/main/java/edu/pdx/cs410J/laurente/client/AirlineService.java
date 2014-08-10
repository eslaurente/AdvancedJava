package edu.pdx.cs410J.laurente.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

/**
 * A GWT remote service that returns a dummy airline
 */
@RemoteServiceRelativePath("flight")
public interface AirlineService extends RemoteService {

  /**
   * Returns the current date and time on the server
   */
  public AbstractAirline addAFlight(String airlineName, AbstractFlight flight);
  public AbstractAirline syncAirline();
}
