package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;
import edu.pdx.cs410J.AirportNames;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**airline.getName()
 * This class implements an AirlineDumper, from which the dump() method writes a file containing data about an airline
 * and a list of its flights. The the data file format is:
 * This is an example of the format of the 'pretty' files:
 *
 * Airline: Hawaiian
 * Number of flights: 1
 * ---------------------------------------------------------------------------------------------------------------------------
 * FLIGHT N0.     SOURCE                  DEPARTURE             DESTINATION             ARRIVAL               DURATION (HOURS)
 * ---------------------------------------------------------------------------------------------------------------------------
 * 14             HNL (Honolulu, HI)      03/02/2014 04:53 PM   HNL (Honolulu, HI)      03/03/2014 01:00 AM   8
 *
 * Note: The writer does not append an valid already-existing file, but rather overwrites the entire file.
 */
public class PrettyPrint implements AirlineDumper {
  private PrintWriter writer;
  private String fileName;
  private String airlineName;

  //Constructors
  /**
   * Constructor that sets up the writer Writer object to point to the the parameter object
   * @param writer    The Writer object that will be used to output an airline's data to a file
   */
  PrettyPrint (PrintWriter writer) {
    this.writer = writer;
    this.airlineName = null;
  }

  /**
   * Constructor with the fileName string that calls the PrettyPrint(File) constructor
   * @param fileName      The fileName that will refer to the file to be written to
   * @throws IOException  Something went wrong when trying to access the file
   */
  PrettyPrint (String fileName) throws IOException {
    this(new File(fileName));
  }

  /**
   * Constructor that wraps a File object with a PrintWriter object. Note that FileWriter(file, false)
   * has a false parameter to overwrite the entire file, rather than appending it.
   * @param file          The File object that points to file to be written and to be wrapped
   * @throws IOException  Something went wrong when trying to  access the file
   */
  PrettyPrint (File file) throws IOException {
    this(new PrintWriter(new FileWriter(file, false)));
    this.fileName = file.getName();
  }

  /**
   * This method prints out the data of an airline's flights' information to a neat table to either a file or standard out
   * @param airline       The airline being written to a destination
   * @throws              java.io.IOException Something went wrong while writing the airline
   */
  @Override
  public void dump(AbstractAirline airline) throws IOException {
    this.airlineName = airline.getName();
    int numOfFlights = airline.getFlights().size();
    int counter = 1;
    StringBuilder toWrite = new StringBuilder();
    toWrite.append("Airline: ").append(this.airlineName).append("\n");
    toWrite.append("Number of flights: ").append(airline.getFlights().size()).append("\n");
    toWrite.append("---------------------------------------------------------------------------------------------------------------------------\n");
    toWrite.append("FLIGHT N0.").append(tabulate(5)).append("SOURCE").append(tabulate(18)).append("DEPARTURE").
      append(tabulate(13)).append("DESTINATION").append(tabulate(13)).append("ARRIVAL").append(tabulate(15)).append("DURATION (HOURS)\n");
    toWrite.append("---------------------------------------------------------------------------------------------------------------------------\n");
    if (numOfFlights > 0) {
      Flight currentFlight;
      Iterator<Flight> iter = airline.getFlights().iterator();
      int flightNumber;
      double flightDuration;
      String srcAirport, destAirport, departure, arrival;
      while (iter.hasNext()) {
        currentFlight = iter.next();
        flightNumber = currentFlight.getNumber();
        srcAirport = currentFlight.getSource() + " (" + AirportNames.getName(currentFlight.getSource()) + ")";
        destAirport = currentFlight.getDestination() + " (" + AirportNames.getName(currentFlight.getDestination()) + ")";
        departure = convertDateTimeToShortForm(currentFlight.getDeparture());
        arrival = convertDateTimeToShortForm(currentFlight.getArrival());
        toWrite.append(flightNumber).append(tabulate(13)).append(srcAirport).append(tabulate(24-srcAirport.length())).append(departure).
          append(tabulate(22-departure.length())).append(destAirport).append(tabulate(24-destAirport.length())).append(arrival).
          append(tabulate(22-departure.length())).append(getFlightDuration(currentFlight.getDeparture(), currentFlight.getArrival())).append("\n");
      }
      writer.print(toWrite.toString());
    }
    else {
      writer.println(toWrite.toString() + "\n**NO FLIGHTS**");
    }
    writer.flush();
    writer.close();
  }

  /**
   * This method creates a customized number of spaces used for tab spacing, using int parameter to determine
   * how many spaces to append.
   * @param numOfSpaces The number of spaces to concatenate
   * @return The space String object from the tabs object
   */
  private static String tabulate(int numOfSpaces) {
    StringBuilder tabs = new StringBuilder();
    for (int i = 0; i < numOfSpaces; ++i) {
      tabs.append(" ");
    }
    return tabs.toString();
  }

  /**
   * This method retrieves the current File object's file name
   * @return    The file name that is to be written to
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * This method converts a date into the the format mm/dd/yyyy hh:mm am|pm string format
   * @param date    The Date object to be formatted
   * @return        The String representation of the formatted date
   */
  public static String convertDateTimeToShortForm(Date date) {
    SimpleDateFormat shortForm = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    shortForm.setLenient(false);
    return shortForm.format(date);
  }

  /**
   * This method calculates the duration of the flight from a filght's departure to arrival date/time values
   * @param depart    The departure date/time of the flight
   * @param arrive    The arrival date/time of the flight
   * @return          The flight duration in long
   */
  private long getFlightDuration(Date depart, Date arrive) {
    return (arrive.getTime() - depart.getTime()) / (1000 * 3600);
  }
}
