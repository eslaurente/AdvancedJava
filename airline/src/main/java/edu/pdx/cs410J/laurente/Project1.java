package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;

/**
 * The main class for the CS410J airline Project
 */
public class Project1 {

    public final String USAGE_NAME = "name" + tabulate(4) + "The name of the airline";
    public final String USAGE_FLIGHTNUMBER = "flightNumber" + tabulate(4) + "The flight number";
    public final String USAGE_SRC = "src" + tabulate(4) + "Three-letter code of departure airport";
    public final String USAGE_DEPARTTIME = "departTime" + tabulate(4) + "Departure date and time (24-hour time)";
    public final String USAGE_DEST = "dest" + tabulate(4) + "Three-letter code of arrival airport";
    public final String USAGE_ARRIVAL = "arrival" + tabulate(4) + "Arrival date and time (24-hour time)";
    public final String USAGE_PRINT = "-print" + tabulate(4) + "Prints a description of the new flight";
    public final String USAGE_README = "-README" + tabulate(4) + "Prints a README for this project and exits";

    public static void main(String[] args) {
    Class c = AbstractAirline.class;  // Refer to one of Dave's classes so that we can be sure it is on the classpath
    System.err.println("Missing command line arguments");
    for (String arg : args) {
        System.out.println(arg);
    }
        System.exit(1);
    }

    public void printUsage() {
        System.out.println(buildUsageString());
    }

    private String buildUsageString() {
        StringBuilder usage = new StringBuilder();
        usage.append("usage: java edu.pdx.cs410J.laurente.Project1 [options] <args>\n");
        usage.append("\targs are (in this order):\n");
        usage.append(USAGE_NAME + "\n");
        usage.append(USAGE_FLIGHTNUMBER + "\n");
        usage.append(USAGE_SRC + "\n");
        usage.append(USAGE_DEPARTTIME + "\n");
        usage.append(USAGE_DEST + "\n");
        usage.append(USAGE_ARRIVAL + "\n");
        usage.append("\toptions are (options may appear in any order):\n");
        usage.append(USAGE_PRINT + "\n");
        usage.append(USAGE_README + "\n");
        return usage.toString();
    }


    public String tabulate(int numOfTabs) {
      StringBuilder tabs = new StringBuilder();
      for (int i = 0; i < numOfTabs; ++i) {
          tabs.append("\t");
      }
      return tabs.toString();
    }

}