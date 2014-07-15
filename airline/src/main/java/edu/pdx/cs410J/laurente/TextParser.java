package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineParser;
import edu.pdx.cs410J.ParserException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.*;

/**
 * Created by emerald on 7/13/14.
 */
public class TextParser implements AirlineParser {
  private LineNumberReader lineReader;
  private Airline airline;
  private String airlineName;
  private int numberOfFlights;

  /*
    Constructors
   */
  TextParser (FileReader file) {
    this.lineReader = new LineNumberReader(file);
    this.airline = null;
    this.airlineName = null;
    this.numberOfFlights = 0;
  }

  TextParser (File file) throws FileNotFoundException {
    this(new FileReader(file));
  }

  TextParser (String fileName) throws FileNotFoundException{
    this(new File(fileName));
  }

  /**
   * Parses some source and returns an airline.
   *
   * @throws edu.pdx.cs410J.ParserException If the source is malformatted.
   */
  @Override
  public AbstractAirline parse() throws ParserException {
    try {
      while (this.lineReader.ready()) {
        //parse airline header (the airline name and the number of flights)
        parseAirlineHeader(this.lineReader.readLine());
        this.airline = new Airline(this.airlineName); //create an empty airline
        //parse each flight the airline has
        for (int i = 0; i < this.numberOfFlights; ++i) {
          this.airline.addFlight(parseAFlight(this.lineReader.readLine())); //parse each flight read and add it to the airline
        }
      }
    } catch (IOException e) {
      throw new ParserException("Parsing error: something went wrong in line " + this.lineReader.getLineNumber());
    }
    return this.airline;
  }

  private void parseAirlineHeader(String header) throws ParserException {
    String[] airlineHeader = header.split("\\s");
    if (airlineHeader.length != 2) {
      throw new ParserException("File malformatted: Header must have the airline name and the number of flights in line " + this.lineReader.getLineNumber());
    }
    this.airlineName = airlineHeader[0];
    try {
      this.numberOfFlights = Integer.parseInt(airlineHeader[1]);
    } catch (NumberFormatException e) {
      throw new ParserException("File malformatted: The airline's number of flight in the header is not an integer");
    }
  }

  private Flight parseAFlight(String flightInfoLine) throws ParserException {
    String flightNumber, source, departDate, departTime, departure, dest, arrivalDate, arrivalTime, arrival;
    String[] flightData = flightInfoLine.split("\\s");
    if (flightData.length != 7) {
      throw new ParserException("File malformatted: flight in line " + this.lineReader.getLineNumber() + " should have 7 strings");
    }
    //parse flight number
    flightNumber = parseFlightNumber(flightData[0]);
    //parse flight source airport code
    source = flightData[1];
    parseAirportCode(source);
    //parse flight departure date and time
    departDate = flightData[2];
    departTime = flightData[3];
    departure = departDate + " " + departTime;
    parseDateAndTime(departure);
    //parse flight destination airport code
    dest = flightData[4];
    parseAirportCode(dest);
    //parse flight arrival time
    arrivalDate = flightData[5];
    arrivalTime = flightData[6];
    arrival = arrivalDate + " " + arrivalTime;
    parseDateAndTime(arrival);
    return new Flight(flightNumber, source, departure, dest, arrival);
  }

  private String parseFlightNumber(String fNumber) throws ParserException {
    int flightNumber;
    try {
      flightNumber = Integer.parseInt(fNumber);
    } catch (NumberFormatException e) {
      throw new ParserException("File malformatted: error parsing the flight number in line " + this.lineReader.getLineNumber() + " ");
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
      throw new ParserException("File malformatted: error parsing departure date and time in line " + this.lineReader.getLineNumber());
    }
  }

  private void parseAirportCode(String airportCode) throws ParserException {
    if (airportCode == null || airportCode.equals("") || airportCode.length() != 3) {
      throw new ParserException("File malformatted: aiport code is invalid in line " + this.lineReader.getLineNumber());
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
