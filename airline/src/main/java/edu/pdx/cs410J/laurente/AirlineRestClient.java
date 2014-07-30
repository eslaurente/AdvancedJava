package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.util.Arrays;

/**
* A helper class for accessing the rest client.  Note that this class provides
* an example of how to make gets and posts to a URL.  You'll need to change it
* to do something other than just send key/value pairs.
*/
public class AirlineRestClient extends HttpRequestHelper
{
  private static final String WEB_APP = "airline";
  private static final String SERVLET = "flights";
  public static final String NAME = "name";
  public static final String FLIGHT_NUMBER = "flightNumber";
  public static final String SRC = "src";
  public static final String DEPART_TIME = "departTime";
  public static final String DEST = "dest";
  public static final String ARRIVE_TIME = "arriveTime";

  private final String url;


  /**
   * Creates a client to the airline REST service running on the given host and port
   * @param hostName The name of the host
   * @param port The port
   */
  public AirlineRestClient( String hostName, int port )
  {
    this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
  }

  /**
   * Returns all keys and values from the server
   */
  public Response getAllKeysAndValues() throws IOException
  {
    return get(this.url );
  }

  /**
   * Returns all values for the given key
   */
  public Response getValues( String key ) throws IOException
  {
    return get(this.url, "key", key);
  }

  public Response addKeyValuePair( String key, String value ) throws IOException
  {
    return post( this.url, "key", key, "value", value );
  }

  public Response getFlightsWithSameSrcAndDest(String... args) throws IOException {
    return get(this.url, NAME, args[0], SRC, args[1], DEST, args[2]);
  }

  public Response addFlight(String... args) throws IOException {
    return post(this.url, NAME, args[0], FLIGHT_NUMBER, args[1], SRC, args[2], DEPART_TIME, args[3], DEST, args[4], ARRIVE_TIME, args[5]);
  }

  public String getUrl() {
    return new String(this.url);
  }
}
