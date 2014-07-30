package edu.pdx.cs410J.laurente;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class AirlineServlet extends HttpServlet
{
  private Airline anAirline = null;
  private final Map<String, String> data = new HashMap<String, String>();

  @Override
  public void init(ServletConfig config) throws ServletException {
    this.anAirline = new Airline();
  }


  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
    response.setContentType( "text/plain" );
    String uri = request.getRequestURI();
    String lastPart = uri.substring(uri.lastIndexOf('/') + 1, uri.length());
    System.out.println("uri: " + request.getRequestURI());
    System.out.println("query string: " + request.getQueryString());
    System.out.println("lastPart = " + lastPart);
    String query = request.getQueryString();
    int parameterCount = getParamsCount(query, "\\.?\\.");
    if (parameterCount == 1) { //query to print all flights of an airline
      String airlineName = getParameter("name", request);
      if (airlineName != null) {
        try {
          writeAllFlights(response, airlineName);
        } catch (IOException e) {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred when printing: " + e.getMessage());
        }
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Airline name missing");
      }
    } else if (parameterCount > 1) {
      String airlineName, src, dest;
      airlineName = getParameter("name", request);
      src = getParameter("src", request);
      dest = getParameter("dest", request);
      if (airlineName != null || src != null || dest != null) {
        writeFlightWithSrcAndDest(response, airlineName, src, dest);
      }
      else {
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Missing parameters: request combination not supported");
      }
    }
  }

  private void writeFlightWithSrcAndDest(HttpServletResponse response, String airlineName, String src, String dest)
          throws ServletException, IOException {
    checkAirlineName(response, airlineName); //check airline name's validity
    if (this.anAirline == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Airline name does not exist");
    }
    else if (this.anAirline.getFlights().size() <= 0) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An airline with no flights encountered");
    }
    Airline airlineTemp = null;
    List<Flight> listOfFlights = new ArrayList<Flight>();
    for (Object anObj: this.anAirline.getFlights()) {
      Flight aFlight = ((Flight) anObj);
      if (aFlight.getSource().equals(src) && aFlight.getDestination().equals(dest)) {
        if (!listOfFlights.contains(aFlight)) { //disallow duplicates
          listOfFlights.add(aFlight);
        }
      }
    }
    airlineTemp = new Airline(airlineName);
    for (Flight aFlight : listOfFlights) {
      airlineTemp.addFlight(aFlight);
    }
    new PrettyPrint(new PrintWriter(response.getWriter())).dump(airlineTemp);
  }

  private void checkAirlineName(HttpServletResponse response, String airlineName) throws IOException {
    if (!airlineName.equals(this.anAirline.getName())) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Airline name does not exist");
    }
    //else airline names match and fall through
  }

  private void writeAllFlights(HttpServletResponse response, String airlineName) throws ServletException, IOException {
    if (this.anAirline == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No airline exists");
    }
    checkAirlineName(response, airlineName); //check airline name validity
    //retrieve flight(s) and write to output
    new PrettyPrint(new PrintWriter(response.getWriter())).dump(this.anAirline);
  }

  private int getParamsCount(String str, String regex) {
    return str.split(regex).length;
  }

  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
    response.setContentType( "text/plain" );

    String key = getParameter( "key", request );
    if (key == null) {
      missingRequiredParameter( response, key );
      return;
    }

    String value = getParameter( "value", request );
    if ( value == null) {
      missingRequiredParameter( response, value );
      return;
    }

    this.data.put(key, value);

    PrintWriter pw = response.getWriter();
    pw.println(Messages.mappedKeyValue(key, value));
    pw.flush();

    response.setStatus( HttpServletResponse.SC_OK);
  }

  private void missingRequiredParameter( HttpServletResponse response, String key )
      throws IOException
  {
    PrintWriter pw = response.getWriter();
    pw.println( Messages.missingRequiredParameter(key));
    pw.flush();

    response.setStatus( HttpServletResponse.SC_PRECONDITION_FAILED );
  }

  private void writeValue( String key, HttpServletResponse response ) throws IOException
  {
    System.out.println("Sending value for " + key + " to client");
    String value = this.data.get(key);

    PrintWriter pw = response.getWriter();
    pw.println(Messages.getMappingCount( value != null ? 1 : 0 ));
    pw.println(Messages.formatKeyValuePair( key, value ));

    pw.flush();

    response.setStatus( HttpServletResponse.SC_OK );
  }

  private void writeAllMappings( HttpServletResponse response ) throws IOException
  {
    PrintWriter pw = response.getWriter();
    pw.println(Messages.getMappingCount( data.size() ));

    for (Map.Entry<String, String> entry : this.data.entrySet()) {
      pw.println(Messages.formatKeyValuePair(entry.getKey(), entry.getValue()));
    }

    pw.flush();

    response.setStatus( HttpServletResponse.SC_OK );
  }

  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;
    } else {
      return value;
    }
  }

  private void writeThisAirline( HttpServletResponse response) throws IOException{
    new PrettyPrint(response.getWriter()).dump(this.anAirline);
  }
}
