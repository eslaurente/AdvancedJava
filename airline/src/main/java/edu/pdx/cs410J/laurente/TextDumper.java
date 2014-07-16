package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;

import java.io.*;
import java.util.Iterator;

/**
 * This class implements an AirlineDumper, from which the dump() method writes a file containing data about an airline
 * and a list of its flights. The the data file format is:
 * The format for writing and reading airline data from a file is:
 * <airline name> <number of flights> flight(s):
 *     <flight1 flight number> <source> <depart date> <depart time> <dest> <arrive date> <arrive time>
 *     <flight2 flight number> <source> <depart date> <depart time> <dest> <arrive date> <arrive time>
 *     etc...<end of file>
 * Note: The writer does not append an valid already-existing file, but rather overwrites the entire file.
 */
public class TextDumper implements AirlineDumper {
  private PrintWriter writer;
  private String fileName;
  private String airlineName;

  //Constructors
  /**
   * Constructor that sets up the writer Writer object to point to the the parameter object
   * @param writer    The Writer object that will be used to output an airline's data to a file
   */
  TextDumper (PrintWriter writer) {
    this.writer = writer;
    this.airlineName = null;
  }

  /**
   * Constructor with the fileName string that calls the TextDumper(File) constructor
   * @param fileName      The fileName that will refer to the file to be written to
   * @throws IOException  Something went wrong when trying to access the file
   */
  TextDumper (String fileName) throws IOException {
    this(new File(fileName));
  }

  /**
   * Constructor that wraps a File object with a PrintWriter object. Note that FileWriter(file, false)
   * has a false parameter to overwrite the entire file, rather than appending it.
   * @param file          The File object that points to file to be written and to be wrapped
   * @throws IOException  Something went wrong when trying to  access the file
   */
  TextDumper (File file) throws IOException {
    this(new PrintWriter(new FileWriter(file, false)));
    this.fileName = file.getName();
  }

  /**
   * This method writes an airline's data to a file
   * @param     airline The airline being written to a destination
   * @throws    java.io.IOException Something went wrong while writing the airline
   */
  @Override
  public void dump(AbstractAirline airline) throws IOException {
    this.airlineName = airline.getName();
    StringBuilder toWrite = new StringBuilder();
    if (airlineName == null || airline.equals("")) {
      throw new IOException("Error: could not retrieve the airline's name");
    }
    int numOfFlights = airline.getFlights().size();
    toWrite.append(airline.getName()).append(" ").append(numOfFlights).append(" flight(s):\n");
    if (numOfFlights > 0) {
      Flight currentFlight;
      Iterator<Flight> iter = airline.getFlights().iterator();
      while (iter.hasNext()) {
        currentFlight = iter.next();
        toWrite.append("    ").append(currentFlight.getNumber()).append(" ");
        toWrite.append(currentFlight.getSource()).append(" ");
        toWrite.append(currentFlight.getDepartureString()).append(" ");
        toWrite.append(currentFlight.getDestination()).append(" ");
        toWrite.append(currentFlight.getArrivalString()).append("\n");
      }
      writer.print(toWrite.toString());
      writer.flush();
      writer.close();
    }
  }

  /**
   * This method returns the file name retrieved by TextDumper(File) constructor.
   * @return    The File object's file name that points to the data being written to
   */
  public String getFileName() {
    return this.fileName;
  }
}
