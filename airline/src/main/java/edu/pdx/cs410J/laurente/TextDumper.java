package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;

import java.io.*;

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
    FileOutputStream file = null;
    try {
      file = new FileOutputStream("airline.dat");
    } catch (FileNotFoundException e) {

    } catch (IOException e) {

    }
  }
}
