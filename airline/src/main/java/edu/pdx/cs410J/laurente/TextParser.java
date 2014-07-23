package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineParser;
import edu.pdx.cs410J.ParserException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.io.*;
import java.util.*;

/**
 * This class implements an AirlineParser, from which the parse() method retrieves an airline's data and its flight(s)
 * from a valid file and parses the specific data and checks for malformatted patterns that invalidate the file. A new
 * Airline object is created from the parsing.
 * The general format for reading and parsing from a file is:
 * <airline name> <number of flights> flight(s):
 *     <flight1 flight number> <source> <depart date> <depart time> <dest> <arrive date> <arrive time>
 *     <flight2 flight number> <source> <depart date> <depart time> <dest> <arrive date> <arrive time>
 *     etc...<end of file>
 */
public class TextParser implements AirlineParser {
  private LineNumberReader lineReader;
  private Airline airline;
  private String airlineName;
  private String expectedAirlineName; //used to check if the name provided from the command line matches the file
  private int numberOfFlights;
  private boolean fileIsEmpty;
  private static final int MAX_NUMBER_OF_FLIGHT_ARGS = 9;

  //Constructors
  /**
   * Constructor that sets up the class field members.
   * @param file                  The FileReader object that is used to access the file and to be wrapped by a
   *                              LineNumberReader object
   * @param expectedAirlineName   The airline name that is passed from the command line. This is used to check for
   *                              mismatching names with the Airline object's name
   */
  TextParser (FileReader file, String expectedAirlineName) {
    this.lineReader = new LineNumberReader(file);
    this.airline = null;
    this.airlineName = null;
    this.expectedAirlineName = expectedAirlineName;
    this.numberOfFlights = 0;
    this.fileIsEmpty = false;
  }

  /**
   * Constructor that calls the TextParser(FileReader) constructor and wraps a File object with it
   * @param file                    The File ojbect that refers to the source data file for parsing
   * @param expectedAirlineName     The expected airline name
   * @throws FileNotFoundException  Something went wrong when trying to create or access the file
   */
  TextParser (File file, String expectedAirlineName) throws FileNotFoundException {
    this(new FileReader(file), expectedAirlineName);
  }

  /**
   * Constructor that calls the TextParser(File) constructor
   * @param fileName                The fileName that refers to the source data file for parsing
   * @param expectedAirlineName     The expected airline name
   * @throws FileNotFoundException  Something went wrong when trying to create a new File ojbect
   */
  TextParser (String fileName, String expectedAirlineName) throws FileNotFoundException{
    this(new File(fileName), expectedAirlineName);
  }

  /**
   * This method parses a source data file and creates a new Airline object with from its flight information parsed
   * individually
   * @return                  The newly-created airline from parsing. A null value will be returned if any uncaught
   *                          parsing exceptions propagate and will be caught outside of this class
   * @throws ParserException  Thrown if the file is malformatted or IOException is caught during parsing
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
      this.fileIsEmpty = true;
    }
    return this.airline;
  }

  /**
   * This method parses the first line of the source data file for the airline header and extracts the airline name and
   * the number of flights the airline should have
   * @param header            The header line of the source data file
   * @throws ParserException  Thrown if any file-malformatted case is encountered
   */
  public void parseAirlineHeader(String header) throws ParserException {
    List<String> headerArray = new ArrayList<String>(Arrays.asList(header.split("\\s")));
    List<String> nameSubList = null;
    int indexOfFlightsWord = headerArray.indexOf("flight(s):");
    if (indexOfFlightsWord == -1) {
      throw new ParserException("File malformatted: No airline header found");
    }
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
      throw new ParserException("File malformatted: " +  this.expectedAirlineName + " airline name does not match name, " + this.airlineName + ", in file");
    }
  }

  /**
   * This method parses a flight data line from the file, validating each element in the line for their respective formats
   * @param flightInfoLine    The current line to be parsed as a string
   * @return                  The new Flight object created from a successful parsing of a line from a valid airline file
   * @throws ParserException  Thrown if any of the helper methods throws any ParserException, and caught in the parse() method
   */
  private Flight parseAFlight(String flightInfoLine) throws ParserException {
    String flightNumber, source, departDate, departTime, dest, arrivalDate, arrivalTime;
    Date departure, arrival;
    List<String> flightData = new ArrayList<String>(Arrays.asList(flightInfoLine.split("\\s")));
    flightData.removeAll(Arrays.asList("", null)); //remove empty elements from flightData arrays list
    if (flightData.size() < MAX_NUMBER_OF_FLIGHT_ARGS) {
      throw new ParserException("File malformatted: a flight should have " + MAX_NUMBER_OF_FLIGHT_ARGS + " arguments for data, but current flight only has " + flightData.size());
    }
    else if (flightData.size() > MAX_NUMBER_OF_FLIGHT_ARGS) {
      throw new ParserException("File malformatted: extraneous arguments found when there should only be " + MAX_NUMBER_OF_FLIGHT_ARGS);
    }
      //parse flight number
      flightNumber = parseFlightNumber(flightData.get(0));
      //parse flight source airport code
      source = flightData.get(1);
      parseAirportCode(source);
      //parse flight departure date and time
      departDate = flightData.get(2);
      departTime = flightData.get(3) + " " + flightData.get(4);
      departure = parseDateAndTime(departDate + " " + departTime);
      //parse flight destination airport code
      dest = flightData.get(5);
      parseAirportCode(dest);
      //parse flight arrival time
      arrivalDate = flightData.get(6);
      arrivalTime = flightData.get(7) + " " + flightData.get(8);
      arrival = parseDateAndTime(arrivalDate + " " + arrivalTime);
    return new Flight(flightNumber, source, departure, dest, arrival);
  }

  /**
   * This method validates a flight's flight number
   * @param fNumber           The flight number as a string
   * @return                  The string value of the flightNumber
   * @throws ParserException  Thrown if fNumber is not a valid integer
   */
  private String parseFlightNumber(String fNumber) throws ParserException {
    int flightNumber;
    try {
      flightNumber = Integer.parseInt(fNumber);
    } catch (NumberFormatException e) {
      throw new ParserException("File malformatted: error parsing the flight number");
    }
    return String.valueOf(flightNumber);
  }

  /**
   * This method validates a date and time combination string by using SimpleDateFormat and Date objects
   * @param dateAndTimeString   The date and time arguments concatenated for parsing
   * @throws ParserException    Throws an exception if the file contains an invalid date and time format string
   */
  private Date parseDateAndTime(String dateAndTimeString) throws ParserException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a"); //capital HH means to use 24-hour format
    dateFormat.setLenient(false); //to disallow dates like 03/33/2014, etc
    Date formattedDate;
    try {
      formattedDate = dateFormat.parse(dateAndTimeString);
      //dateFormat.format(formattedDate);
    } catch (ParseException e) {
      throw new ParserException("File malformatted: error parsing date and time arguments");
    }
    return formattedDate;
  }

  /**
   * This method validates an airport code retrieved from the file
   * @param airportCode       The airport code string
   * @throws ParserException  Throws an exception if airportCode is either null, empty, or is not a 3-letter string
   */
  private void parseAirportCode(String airportCode) throws ParserException {
    if (airportCode == null || airportCode.equals("") || airportCode.length() != 3) {
      throw new ParserException("File malformatted: airport code is invalid");
    }
  }

  /**
   * This method retrieves a reference to the current airline. Note that the returned object needs to be cast as an Airline
   * @return    The reference to the current airline Airline object
   */
  public AbstractAirline getParsedAirline() {
    return this.airline;
  }

  /**
   * This method retrieves the current airline's name
   * @return    The current airline's name
   */
  public String getAirlineName () {
    return this.airlineName;
  }

  /**
   * This method retrieves the number of flights the current airline has
   * @return    The number of flights the current airline has
   */
  public int getNumberOfFlights () {
    return this.numberOfFlights;
  }

  /**
   * This method checks to see if the parser object holds a File object that is empty
   * @return    The boolean of whether the current file in reference is empty
   */
  public boolean fileIsEmpty() {
    return this.fileIsEmpty;
  }
}
