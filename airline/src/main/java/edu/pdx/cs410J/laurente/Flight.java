package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractFlight;

/**
 * Created by emerald on 7/2/14.
 */
public class Flight extends AbstractFlight {
    private String source;
    private String destin;

    Flight() {
        this.source = null;
        this.destin = null;
    }

    Flight (String source, String dest) {
        this.source = source;
        this.destin = dest;
    }

    @Override
    public int getNumber() {
        return 0;
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public String getDepartureString() {
        return null;
    }

    @Override
    public String getDestination() {
        return null;
    }

    @Override
    public String getArrivalString() {
        return null;
    }
}
