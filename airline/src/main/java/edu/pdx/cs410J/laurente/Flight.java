package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractFlight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class represents a flight information of an airline. Flight extends AbstractFlight. It stores information
 * about a flight's number, airport source code, departure date/time, airport destination code, and arrival date/time.
 */
public class Flight extends AbstractFlight {
  private String source; //Must be a three-letter code string
  private String destin; //Must be a three-letter code string
  private Date departInfo;
  private Date arriveInfo;
  private int flightNum;

  /**
   * No-arg constructor
   */
  Flight() {
    this.source = null;
    this.destin = null;
    this.flightNum = -1;
    this.departInfo = null;
    this.arriveInfo = null;
  }

  /**
   * The constructor with the flight number as an int, along with other parameters
   * @param flightNum     The integer value of the flight's ID number
   * @param source        The flight's source airport code
   * @param departInfo    The scheduled departure date and time
   * @param dest          The flight's destination airport code
   * @param arriveInfo    The scheduled departure date and time
   */
  Flight (int flightNum, String source, Date departInfo, String dest, Date arriveInfo) {
    this.flightNum = flightNum;
    this.source = source;
    this.destin = dest;
    this.departInfo = departInfo;
    this.arriveInfo = arriveInfo;
  }

  /**
   * The constructor with the flight number as a String, along with other parameters
   * @param flightNum     The String value representation of the flight number
   * @param source        The flight's source airport code
   * @param departInfo    The scheduled departure date and time
   * @param dest          The flight's destination airport code
   * @param arriveInfo    The scheduled departure date and time
   */
  Flight (String flightNum, String source, Date departInfo, String dest, Date arriveInfo) {
    this.flightNum = Integer.parseInt(flightNum);
    this.source = source;
    this.destin = dest;
    this.departInfo = departInfo;
    this.arriveInfo = arriveInfo;
  }

  /**
   * This method sets the flight's dscheduled departure date and time
   * @param departure   The scheduled departure date and time
   */
  public void setDepartInfo(Date departure) {
    this.departInfo = departure;
  }

  /**
   * This method sets the flight's scheduled arrival date and time
   * @param arrival   The scheduled arrival date and time
   */
  public void setArriveInfo(Date arrival) {
    this.arriveInfo = arrival;
  }

  /**
   * This method retrieves this flight's flight number as an int
   * @return    The int value of this flight's flight number
   */
  @Override
  public int getNumber() {
    return this.flightNum;
  }

  /**
   * This method retrieves this flight's source airport code
   * @return    This flight's source airport code
   */
  @Override
  public String getSource() {
    return this.source;
  }

  /**
   * This method retrieves this flight's scheduled departure date and time
   * @return    This flight's departure date and time info
   */
  @Override
  public String getDepartureString() {
    return parseDateTimeToString(this.departInfo);
  }

  /**
   * Returns this flight's departure time as a <code>Date</code>.
   */
  @Override
  public Date getDeparture() {
    return this.departInfo;
  }

  /**
   * This method retrieves this flight's destination airport code
   * @return    This flight's destination airport code
   */
  @Override
  public String getDestination() {
    return this.destin;
  }

  /**
   * This method retrieves this flight's scheduled arrival date and time
   * @return    This flight's arrival date and time info
   */
  @Override
  public String getArrivalString() {
    return parseDateTimeToString(this.arriveInfo);
  }

  /**
   * Returns this flight's arrival time as a <code>Date</code>.
   */
  @Override
  public Date getArrival() {
    return this.arriveInfo;
  }

  private String parseDateTimeToString(Date date) {
    String arrivalString;
    SimpleDateFormat shortForm = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    shortForm.setLenient(false);
    try {
      arrivalString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(shortForm.format(date)).toString();
    } catch (ParseException e) {
      System.err.println("Error converting date/time to string: " + e.getMessage());
      System.exit(1);
      return null;
    }
    return arrivalString;
  }


}
