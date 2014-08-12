package edu.pdx.cs410J.laurente.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

/**
 * The client-side interface to the ping service
 */
public interface AirlineServiceAsync {

  /**
   * Return the current date/time on the server
   */
  void addAFlight(String airlineName, AbstractFlight flight, AsyncCallback<AbstractAirline> async);
  void syncAirline(AsyncCallback<AbstractAirline> async);
  void deleteFlight(AbstractFlight flight, AsyncCallback<Boolean> async);
}
