  package edu.pdx.cs410J.laurente;

  import edu.pdx.cs410J.AbstractFlight;

  import java.util.Date;

  /**
  * Created by emerald on 7/2/14.
  */
  public class Flight extends AbstractFlight {
    private String source; //Must be a three-letter code string
    private String destin; //Must be a three-letter code string
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

    Flight (String flightNum, String source, String departInfo, String dest, String arriveInfo) {
      this.flightNum = Integer.parseInt(flightNum);
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
