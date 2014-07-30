package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AirlineServlet extends HttpServlet
{
  public static final String NAME = "name";
  public static final String FLIGHT_NUMBER = "flightNumber";
  public static final String SRC = "src";
  public static final String DEPART_TIME = "departTime";
  public static final String DEST = "dest";
  public static final String ARRIVE_TIME = "arriveTime";
  private Airline anAirline = null;
  private final Map<String, String> data = new HashMap<String, String>();

  @Override
  public void init(ServletConfig config) throws ServletException {
    this.anAirline = null;
  }


  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
    response.setContentType( "text/plain" );
    String uri = request.getRequestURI();
    String lastPart = uri.substring(uri.lastIndexOf('/') + 1, uri.length());
    System.out.println("uri: " + request.getRequestURI());
    System.out.println("url: " + request.getRequestURL());
    System.out.println("query string: " + request.getQueryString());
    System.out.println("lastPart = " + lastPart);
    String query = request.getQueryString();
    int parameterCount = getParamsCount(query, "&");
    System.out.println("paramater count = " + parameterCount);
    if (this.anAirline == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No airline exists");
      return;
    }
    if (parameterCount == 1) { //query to print all flights of an airline
      String airlineName = getParameter(NAME, request);
      if (airlineName != null) {
        try {
          writeAllFlights(response, airlineName);
        } catch (IOException e) {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred when printing: " + e.getMessage());
          return;
        }
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Airline name missing");
      }
    } else if (parameterCount == 3) {
      String airlineName, src, dest;
      airlineName = getParameter(NAME, request);
      src = getParameter(SRC, request);
      dest = getParameter(DEST, request);
      if (airlineName != null || src != null || dest != null) {
        try {
          if (AirportNames.getName(src.toUpperCase()) == null) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Invalid airport source code: \"" + src + "\" is not a valid airport");
            return;
          }
          else if (AirportNames.getName(dest.toUpperCase()) == null) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Invalid airport destination code: \"" + dest + "\" is not a valid airport");
            return;
          }
          writeFlightWithSrcAndDest(response, airlineName, src, dest);
        } catch (IOException e) {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred when printing: " + e.getMessage());
          return;
        }
      } else {
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Missing parameters: request combination not supported");
        return;
      }
    } else {
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Missing parameters: request combination not supported");
      return;
    }
    response.setStatus( HttpServletResponse.SC_OK );
  }

  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
    response.setContentType( "text/plain" );
    String query = request.getQueryString();
    String airlineName, flightNumStr, srcStr, departStr, destStr, arriveStr;
    Date departure, arrival;
    int flightNumInt;
    System.out.println("From doPost(): query string = " + query);

    airlineName = getParameter(NAME, request);
    flightNumStr = getParameter(FLIGHT_NUMBER, request);
    srcStr = getParameter(SRC, request);
    departStr = getParameter(DEPART_TIME, request);
    destStr = getParameter(DEST, request);
    arriveStr = getParameter(ARRIVE_TIME, request);

    if ( (valueNotNorEmpty(response, NAME, airlineName) == false) ||
      (valueNotNorEmpty(response, FLIGHT_NUMBER, flightNumStr) == false) ||
      (valueNotNorEmpty(response, SRC, srcStr) == false) ||
      (valueNotNorEmpty(response, DEPART_TIME, departStr) == false) ||
      (valueNotNorEmpty(response, DEST, destStr) == false) ||
      (valueNotNorEmpty(response, ARRIVE_TIME, arriveStr) == false) ) {
      return;
    }
    //Add or check airline name
    if (this.anAirline == null) {
      this.anAirline = new Airline(airlineName);
    }
    else {
      if (checkAirlineName(response, airlineName) == false) {
        return;
      }
    }
    //parse flight number
    try {
      flightNumInt = Integer.parseInt(flightNumStr);
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid flight number");
      return;
    }
    //parse source airport code
    if (srcStr.length() != 3 && (AirportNames.getName(srcStr.toUpperCase()) == null)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid source airport code");
      return;
    }
    //parse departure date and time
    try {
      departure = TextParser.parseDateAndTime(departStr);
    } catch (ParserException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid format for flight's departure date and time");
      return;
    }
    //parse destination airport code
    if (destStr.length() != 3 && (AirportNames.getName(destStr.toUpperCase()) == null)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid destination airport code");
      return;
    }
    //parse arrival date and time
    try {
      arrival = TextParser.parseDateAndTime(arriveStr);
    } catch (ParserException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid format for flight's arrival date and time");
      return;
    }
    //if reached here, then parameters are valid. add new flight
    System.out.println("name: " + airlineName + ", flightNum: " + flightNumStr + ", src: " + srcStr + ", departTime: " + departStr + ", dest: " + destStr + ", arriveTime: " + arriveStr);
    Flight currentFlight = new Flight(flightNumInt, srcStr, departure, destStr, arrival);
    //check if flight already exists
    if (this.anAirline.getFlights().contains(currentFlight)) {
      response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Flight already exists");
      return;
    }
    this.anAirline.addFlight(currentFlight);

    System.out.println("----AIRLINE INFO----\n" + this.anAirline.toString() + "\n\t" + this.anAirline.getFlights().toArray()[0].toString());
    //write as PrettyPrint format
    writeAllFlights(response, this.anAirline.getName());
    response.setStatus( HttpServletResponse.SC_OK);
  }

  private void writeFlightWithSrcAndDest(HttpServletResponse response, String airlineName, String src, String dest)
          throws ServletException, IOException {
    if (checkAirlineName(response, airlineName) == false) {//check airline name's validity
      return;
    }
    else if (this.anAirline.getFlights().size() <= 0) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An airline with no flights encountered");
      return;
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

  private boolean checkAirlineName(HttpServletResponse response, String airlineName) throws IOException {
    if (!airlineName.equals(this.anAirline.getName())) {
      response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Airline name \"" + airlineName + "\" does not exist");
      return false;
    }
    //else airline names match and fall through
    return true;
  }

  private void writeAllFlights(HttpServletResponse response, String airlineName) throws ServletException, IOException {
    if (checkAirlineName(response, airlineName) == false) {//check airline name validity
      return;
    }
    //retrieve flight(s) and write to output
    new PrettyPrint(new PrintWriter(response.getWriter())).dump(this.anAirline);
  }

  private int getParamsCount(String str, String regex) {
    if (str == null) {
      return 0;
    }
    String[] result = str.split(regex);
    return result == null ? 1 : result.length;
  }

  /**
   * This method attempts to parse the date section of the args passed in. The criteria for the date and time format
   * is that it must be in this format: mm/dd/yyyy hh:mm am|pm
   * This method uses the SimpleDateFormat class to parse and format the date/time argument.
   * @param dateTimeArg         The argument that contains the date and time string
   * @return                    The formatted string of the date and time argument.
   * @exception ParseException  An error is thrown if dateTimeArg is not of the form "MM/dd/yyy h:mm a"
   */
  private static Date formatDateTime(String dateTimeArg) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
    dateFormat.setLenient(false); //to disallow dates like 03/33/2014, etc
    Date formattedDate;
    try {
      formattedDate = dateFormat.parse(dateTimeArg);
      //resultingStr.append(dateFormat.format(formattedDate));
    } catch (ParseException e) {
      throw new ParseException("", -1);
    }
    return formattedDate;
  }

  private boolean valueNotNorEmpty(HttpServletResponse response, String key, String value) throws IOException{
    if (value == null || value.equals("")) {
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Missing value for airline's " + key);
      return false;
    }
    return true;
  }

  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;
    } else {
      return value;
    }
  }
}
