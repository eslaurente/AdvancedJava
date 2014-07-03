package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;

/**
 * The main class for the CS410J airline Project
 */
public class Project1 {

  public static final String USAGE_NAME = "name" + tabulate(24) + "The name of the airline";
  public static final String USAGE_FLIGHTNUMBER = "flightNumber" + tabulate(16) + "The flight number";
  public static final String USAGE_SRC = "src" + tabulate(25) + "Three-letter code of departure airport";
  public static final String USAGE_DEPARTTIME = "departTime" + tabulate(18) + "Departure date and time (24-hour time)";
  public static final String USAGE_DEST = "dest" + tabulate(24) + "Three-letter code of arrival airport";
  public static final String USAGE_ARRIVAL = "arrival" + tabulate(21) + "Arrival date and time (24-hour time)";
  public static final String USAGE_PRINT = "-print" + tabulate(22) + "Prints a description of the new flight";
  public static final String USAGE_README = "-README" + tabulate(21) + "Prints a README for this project and exits";

  public static void main(String[] args) {
    Class c = AbstractAirline.class;  // Refer to one of Dave's classes so that we can be sure it is on the classpath
    System.err.println("Missing command line arguments");
    for (String arg : args) {
      System.out.println(arg);
    }
    printUsage();
    System.exit(1);
  }

  public static void printUsage() {
    System.out.println(buildUsageString());
  }

  private static String buildUsageString() {
    StringBuilder usage = new StringBuilder();
    usage.append("usage: java edu.pdx.cs410J.laurente.Project1 [options] <args>\n");
    usage.append("\targs are (in this order):\n");
    usage.append("\t\t").append(USAGE_NAME).append("\n");
    usage.append("\t\t").append(USAGE_FLIGHTNUMBER).append("\n");
    usage.append("\t\t").append(USAGE_SRC).append("\n");
    usage.append("\t\t").append(USAGE_DEPARTTIME).append("\n");
    usage.append("\t\t").append(USAGE_DEST).append("\n");
    usage.append("\t\t").append(USAGE_ARRIVAL).append("\n");
    usage.append("\toptions are (options may appear in any order):\n");
    usage.append("\t\t").append(USAGE_PRINT).append("\n");
    usage.append("\t\t").append(USAGE_README).append("\n");
    return usage.toString();
  }


  public static String tabulate(int numOfTabs) {
    StringBuilder tabs = new StringBuilder();
    for (int i = 0; i < numOfTabs; ++i) {
      tabs.append(" ");
    }
    return tabs.toString();
  }

}