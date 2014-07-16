package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineParser;
import edu.pdx.cs410J.ParserException;

import javax.swing.text.html.parser.Parser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.*;
import java.util.*;

/**
 * Created by emerald on 7/13/14.
 */
public class TextParser implements AirlineParser {
  private LineNumberReader lineReader;
  private Airline airline;
  private String airlineName;
  private String expectedAirlineName;
  private int numberOfFlights;

  /*
    Constructors
   */
  TextParser (FileReader file, String expectedAirlineName) {
    this.lineReader = new LineNumberReader(file);
    this.airline = null;
    this.airlineName = null;
    this.expectedAirlineName = expectedAirlineName;
    this.numberOfFlights = 0;
  }

  TextParser (File file, String expectedAirlineName) throws FileNotFoundException {
    this(new FileReader(file), expectedAirlineName);
  }

  TextParser (String fileName, String expectedAirlineName) throws FileNotFoundException{
    this(new File(fileName), expectedAirlineName);
  }

  /**
   * Parses some source and returns an airline.
   *
   * @throws edu.pdx.cs410J.ParserException If the source is malformatted.
   */
  @Override
  public AbstractAirline parse() throws ParserException {
    String currentLine;
    int numOfFlightLines = 0;
    try {
      while (this.lineReader.ready()) {
        currentLine = this.lineReader.readLine();
        if (currentLine == null) {
          break;
        }
        else if (currentLine.equals("")) {
          continue;
        }
        else if (this.lineReader.getLineNumber() == 1) {
          //parse airline header (the airline name and the number of flights)
          parseAirlineHeader(currentLine);
          this.airline = new Airline(this.airlineName); //create an empty airline
        }
        else {
          //parse each flight the airline has
          //System.err.println("currentLine: " + currentLine);
          this.airline.addFlight(parseAFlight(currentLine)); //parse each flight read and add it to the airline
          ++numOfFlightLines;
        }
      }
    } catch (ParserException e) {
      throw new ParserException(e.getMessage() + " in line number " + this.lineReader.getLineNumber());
    } catch (Exception e) {
      if (e instanceof UnsupportedOperationException) {
        //end of the file has been reached
        if (numOfFlightLines != this.airline.getFlights().size()) {
          throw new ParserException("File malformatted: There are " + numOfFlightLines + " flight entries but there should be " + this.airline.getFlights().size() + " flights for " + this.airlineName + " airlines");
        }
        //else ignore exception and return the Airline airline object
      }
      else if (e instanceof IOException) {
        throw new ParserException("File error: an error occurred at line number " + this.lineReader.getLineNumber());
      }

    }
    if (this.airline == null && this.lineReader.getLineNumber() == 0) {
      throw new ParserException("File malformatted: file is empty");
    }
    return this.airline;
  }

  public void parseAirlineHeader(String header) throws ParserException {
    List<String> headerArray = new ArrayList<String>(Arrays.asList(header.split("\\s")));
    List<String> nameSubList = null;
    int indexOfFlightsWord = headerArray.indexOf("flight(s):");
    int indexOfNumOfFlights = indexOfFlightsWord - 1;
    try {
      this.numberOfFlights = Integer.parseInt(headerArray.get(indexOfNumOfFlights));
    } catch (NumberFormatException e) {
      throw new ParserException("File malformatted: The airline's number of flights in the header must be an integer, but " + headerArray.get(indexOfNumOfFlights) + " is not");
    }
    this.airlineName = "";
    nameSubList = headerArray.subList(0, indexOfNumOfFlights);
    for (int i = 0; i < nameSubList.size(); ++i) {
      this.airlineName += nameSubList.get(i);
      if (i < nameSubList.size() - 1) {
        this.airlineName += " ";
      }
    }
    if (this.airlineName == null || this.airlineName.equals("")) {
      throw new ParserException("File malformatted: The airline header must contain an airline name");
    }
    else if (this.expectedAirlineName != null && (!this.expectedAirlineName.equals(this.airlineName))) {
      throw new ParserException("File malformatted: " + this.airlineName + " airline name in file does not match " + this.expectedAirlineName);
    }
  }

  private Flight parseAFlight(String flightInfoLine) throws ParserException {
    String flightNumber, source, departDate, departTime, departure, dest, arrivalDate, arrivalTime, arrival;
    List<String> flightData = new ArrayList<String>(Arrays.asList(flightInfoLine.split("\\s")));
    //System.err.println("Flight data: " + flightData);
    flightData.removeAll(Arrays.asList("", null)); //remove empty elements from flightData arrays list
    //System.err.println("Flight data: " + flightData);
    if (flightData.size() != 7) {
      throw new ParserException("File malformatted: flights should have 7 pieces of data, but current flight does not have enough");
    }
    //parse flight number
    flightNumber = parseFlightNumber(flightData.get(0));
    //parse flight source airport code
    source = flightData.get(1);
    parseAirportCode(source);
    //parse flight departure date and time
    departDate = flightData.get(2);
    departTime = flightData.get(3);
    departure = departDate + " " + departTime;
    parseDateAndTime(departure);
    //parse flight destination airport code
    dest = flightData.get(4);
    parseAirportCode(dest);
    //parse flight arrival time
    arrivalDate = flightData.get(5);
    arrivalTime = flightData.get(6);
    arrival = arrivalDate + " " + arrivalTime;
    parseDateAndTime(arrival);
    Flight aFlight = new Flight(flightNumber, source, departure, dest, arrival);
    return aFlight;
  }

  private String parseFlightNumber(String fNumber) throws ParserException {
    int flightNumber;
    try {
      flightNumber = Integer.parseInt(fNumber);
    } catch (NumberFormatException e) {
      throw new ParserException("File malformatted: error parsing the flight number");
    }
    return String.valueOf(flightNumber);
  }

  private void parseDateAndTime(String departure) throws ParserException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm"); //capital HH means to use 24-hour format
    dateFormat.setLenient(false); //to disallow dates like 03/33/2014, etc
    Date formattedDate;
    try {
      formattedDate = dateFormat.parse(departure);
    } catch (ParseException e) {
      throw new ParserException("File malformatted: error parsing departure date and time");
    }
  }

  private void parseAirportCode(String airportCode) throws ParserException {
    if (airportCode == null || airportCode.equals("") || airportCode.length() != 3) {
      throw new ParserException("File malformatted: airport code is invalid");
    }
  }

  public AbstractAirline getParsedAirline() {
    return this.airline;
  }

  public String getAirlineName () {
    return this.airlineName;
  }

  public int getNumberOfFlights () {
    return this.numberOfFlights;
  }
}
