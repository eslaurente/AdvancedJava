  package edu.pdx.cs410J.laurente;

  import edu.pdx.cs410J.AbstractFlight;

  /**
  * Created by emerald on 7/2/14.
  */
  public class Flight extends AbstractFlight {
    private String source;
    private String destin;
    private String departInfo;
    private String arriveInfo;
    private int flightNum;

    Flight() {
      this.source = null;
      this.destin = null;
      this.flightNum = -1;
      this.departInfo = null;
      this.arriveInfo = null;
    }

    Flight (int flightNum, String source, String dest, String departInfo, String arriveInfo) {
      this.flightNum = flightNum;
      this.source = source;
      this.destin = dest;
      this.departInfo = departInfo;
      this.arriveInfo = arriveInfo;
    }

    public void setDepartInfo(String departure) {
      this.departInfo = departure;
    }

    public void setArriveInfo(String arrival) {
      this.arriveInfo = arrival;
    }

    @Override
    public int getNumber() {
      return this.flightNum;
    }

    @Override
    public String getSource() {
      return this.source;
    }

    @Override
    public String getDepartureString() {
      return this.departInfo;
    }

    @Override
    public String getDestination() {
      return this.destin;
    }

    @Override
    public String getArrivalString() {
      return this.arriveInfo;
    }
  }
