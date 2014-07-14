package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;

import java.io.*;
import java.util.Iterator;

/**
 * Created by emerald on 7/12/14.
 */
public class TextDumper implements AirlineDumper{
  /**
   * Dumps an airline to some destination.
   *
   * @param airline The airline being written to a destination
   * @throws java.io.IOException Something went wrong while writing the airline
   */
  @Override
  public void dump(AbstractAirline airline) throws IOException {

    Writer writer = null;
    String airlineName = airline.getName();
    StringBuilder toWrite = new StringBuilder();
    if (airlineName == null || airline.equals("")) {
      throw new IOException("Error: could not retrieve the airline's name");
    }
    String fileName = airlineName.replaceAll("\\s", "_") + ".flt";

    try {
      writer = new FileWriter(fileName, true);
      int numOfFlights = airline.getFlights().size();
      toWrite.append(airline.getName()).append(" ").append(numOfFlights).append(" flight(s):\n");
      if (numOfFlights > 0) {
        Flight currentFlight;
        Iterator<Flight> iter = airline.getFlights().iterator();
        while (iter.hasNext()) {
          currentFlight = iter.next();
          toWrite.append("\t").append(currentFlight.getNumber()).append(" ");
          toWrite.append(currentFlight.getSource()).append(" ");
          toWrite.append(currentFlight.getDepartureString()).append(" ");
          toWrite.append(currentFlight.getDestination()).append(" ");
          toWrite.append(currentFlight.getArrivalString()).append(" ").append("\n");
        }
        writer.append(toWrite.toString());
        writer.flush();
        writer.close();
      }

    } catch (FileNotFoundException e) {
      throw new IOException("Error: the file ," + fileName + ", could not be created or was not found");
    } catch (IOException e) {
      throw new IOException("Error: could not create or open the file, " + fileName);
    }
  }
}
