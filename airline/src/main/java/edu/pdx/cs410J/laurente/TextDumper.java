package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;

import java.io.*;
import java.util.Iterator;

/**
 * Created by emerald on 7/12/14.
 */
public class TextDumper implements AirlineDumper {
  private PrintWriter writer;
  private String fileName;
  private String airlineName;
  /*
    Constructors
   */
  TextDumper (PrintWriter writer) {
    this.writer = writer;
    this.airlineName = null;
  }

  TextDumper (String fileName) throws IOException {
    this(new File(fileName));
  }

  TextDumper (File file) throws IOException {
    this(new PrintWriter(new FileWriter(file, false)));
    this.fileName = file.getName();
  }
  /**
   * Dumps an airline to some destination.
   *
   * @param airline The airline being written to a destination
   * @throws java.io.IOException Something went wrong while writing the airline
   */
  @Override
  public void dump(AbstractAirline airline) throws IOException {
    this.airlineName = airline.getName();
    StringBuilder toWrite = new StringBuilder();
    if (airlineName == null || airline.equals("")) {
      throw new IOException("Error: could not retrieve the airline's name");
    }
    String fileName = airlineName.replaceAll("\\s", "_") + ".flt";
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
}
