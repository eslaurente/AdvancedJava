package edu.pdx.cs410J.laurente.client;

import edu.pdx.cs410J.AbstractFlight;
import java.util.Date;
import static com.google.gwt.i18n.shared.DateTimeFormat.*;


/**
 * This class represents a flight information of an airline. Flight extends AbstractFlight. It stores information
 * about a flight's number, airport source code, departure date/time, airport destination code, and arrival date/time.
 */
public class Flight extends AbstractFlight implements Comparable {
  private String source; //Must be a three-letter code string
  private String destin; //Must be a three-letter code string
  private Date departInfo;
  private Date arriveInfo;
  private int flightNum;
  private String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm a";

  /**
   * No-arg constructor
   */
  public Flight() {
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
  public Flight (int flightNum, String source, Date departInfo, String dest, Date arriveInfo) {
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
  public Flight (String flightNum, String source, Date departInfo, String dest, Date arriveInfo) {
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

  /**
   * This method converts a Date object into a string representation in "mm/dd/yyyy hh:mm am|pm" format
   * @param date    The Date object to be parsed
   * @return        The string representation of the formatted date
   */
  private String parseDateTimeToString(Date date) {
    //String arrivalString;
    /*
    SimpleDateFormat shortForm = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    shortForm.setLenient(false);
    try {
      arrivalString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(shortForm.format(date)).toString();
    } catch (ParseException e) {
      System.err.println("Error converting date/time to string: " + e.getMessage());
      System.exit(1);
      return null;
    }
    */

    //return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
    return getFormat(DATE_TIME_FORMAT).format(date);
  }


  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
   * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
   * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
   * <tt>y.compareTo(x)</tt> throws an exception.)
   * <p/>
   * <p>The implementor must also ensure that the relation is transitive:
   * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
   * <tt>x.compareTo(z)&gt;0</tt>.
   * <p/>
   * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
   * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
   * all <tt>z</tt>.
   * <p/>
   * <p>It is strongly recommended, but <i>not</i> strictly required that
   * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
   * class that implements the <tt>Comparable</tt> interface and violates
   * this condition should clearly indicate this fact.  The recommended
   * language is "Note: this class has a natural ordering that is
   * inconsistent with equals."
   * <p/>
   * <p>In the foregoing description, the notation
   * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
   * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
   * <tt>0</tt>, or <tt>1</tt> according to whether the value of
   * <i>expression</i> is negative, zero or positive.
   *
   * @param o the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object.
   * @throws NullPointerException if the specified object is null
   * @throws ClassCastException   if the specified object's type prevents it
   *                              from being compared to this object.
   */
  @Override
  public int compareTo(Object o) {
    int result;
    if (o instanceof Flight) {
      result = this.source.compareTo(((Flight) o).getSource());
      if (result == 0) {
        return this.departInfo.compareTo(((Flight) o).departInfo);
      }
      return result;
    }
    else {
      return -Integer.MIN_VALUE; //invalid case
    }
  }

  /**
   * This method checks for deep-equality of this flight with another flight object
   * @param o     The flight object to be compared
   * @return      Boolean value: true if this flight and flight 'o' are equal; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Flight flight = (Flight) o;
    if (flightNum != flight.flightNum) return false;
    if (!arriveInfo.equals(flight.arriveInfo)) return false;
    if (!departInfo.equals(flight.departInfo)) return false;
    if (!destin.equals(flight.destin)) return false;
    if (!source.equals(flight.source)) return false;
    return true;
  }
}
