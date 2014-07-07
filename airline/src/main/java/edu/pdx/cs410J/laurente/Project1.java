package edu.pdx.cs410J.laurente;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The main class for the CS410J airline Project
 */
public class Project1 {

  public static final String USAGE_NAME = "name" + tabulate(24) + "The name of the airline";
  public static final String USAGE_FLIGHTNUMBER = "flightNumber" + tabulate(16) + "The flight number";
  public static final String USAGE_SRC = "src" + tabulate(25) + "Three-letter code of departure airport";
  public static final String USAGE_DEPARTTIME = "departTime" + tabulate(18) + "Departure date and time (24-hour time)";
  public static final String USAGE_DEST = "dest" + tabulate(24) + "Three-letter code of arrival airport";
  public static final String USAGE_ARRIVAL = "arrivalTime" + tabulate(21) + "Arrival date and time (24-hour time)";
  public static final String USAGE_PRINT = "-print" + tabulate(22) + "Prints a description of the new flight";
  public static final String USAGE_README = "-README" + tabulate(21) + "Prints a README for this project and exits";
  public static final String OPTION_PRINT = "-print";
  public static final String OPTION_README = "-README";
  public static final String VERBOSE_USAGE = buildUsageString();

  public static void main(String[] args) {
    Airline anAirline = null;
    String name, flightNumber, src, departTime, dest, arrivalTime;
    int argStartingPosition = 0; //Actual starting index offset by the number of options
    List<String> options;
    //Class c = AbstractAirline.class;  // Refer to one of Dave's classes so that we can be sure it is on the classpath
    //System.err.print("Missing command line arguments");

    //Parse option arguments and collect them.
    try {
      options = getOptions(args);
      argStartingPosition = options.size(); //offset the rest of arguments starting index
    } catch (ParseException e) {
      printUsageMessageError(e.getMessage());
      System.exit(1);
      throw new AssertionError("Unreachable statement reached.");
    }

    if (args.length - argStartingPosition < 6) {
      printUsageMessageError("Invalid argument: Not enough information about the flight was provided");
      System.exit(1);
    }
    else if (options.contains(OPTION_README)) { //since README has high precedence, display it ignore parsing
      printReadme();
      System.exit(0);
    }
    //--Begin parsing the rest of the arguments --
    //Get name
    name = args[argStartingPosition];
    if (name.equals("")) {
      printUsageMessageError("Invalid argument: Airline name cannot be empty");
      System.exit(1);
    }
    //Get flight number
    try {
      flightNumber = args[argStartingPosition + 1];
      int unused = Integer.parseInt(flightNumber);
    } catch (NumberFormatException e) {
      printUsageMessageError("Invalid argument: flight number must be a valid integer");
      System.exit(1);
      throw new AssertionError("Unreachable statement reached.");

    }
    //Get the source airport code
    src = args[argStartingPosition + 2].toUpperCase(); //use upper-case format
    if (!isValidAirportCode(src) || src.equals("")) {
      printUsageMessageError("Error: the source airport code \"" + src + "\" is not a valid code");
      System.exit(1);
    }
    //Get departure date and time
    try {
      departTime = formatDateTime(args[argStartingPosition + 3]);
    } catch (ParseException e) {
      printUsageMessageError(e.getMessage());
      System.exit(1);
      throw new AssertionError("Unreachable statement reached.");
    }
    //Get destination airport code
    dest = args[argStartingPosition + 4].toUpperCase(); //use upper-case format
    if (!isValidAirportCode(dest) || dest.equals("")) {
      printUsageMessageError("Error: the destination airport code \"" + dest + "\" is not a valid code");
      System.exit(1);
    }
    //Get arrival date and time
    try {
      arrivalTime = formatDateTime(args[argStartingPosition + 5]);
    } catch (ParseException e) {
      printUsageMessageError(e.getMessage());
      System.exit(1);
      throw new AssertionError("Unreachable statement reached.");
    }
    //Create airline object
    anAirline = new Airline(name, new Flight(flightNumber, src, departTime, dest, arrivalTime));
    if (anAirline == null) {
      printUsageMessageError("Error: Something serious went wrong");
      System.exit(1);
    }
    if (options.contains(OPTION_PRINT)) { //Print airline info to standard out if there is -print
      printAirlineFlightInfo(anAirline);
    }
    System.exit(0);
  }

  public static void printAirlineFlightInfo(Airline anAirline) {
    System.out.println(anAirline.toString() + ": " + anAirline.getFlights().toArray()[0].toString());
  }

  public static List<String> getOptions(String[] args) throws ParseException{
    List<String> options = new ArrayList<String>();
    int nonOptionArgCount = 0;
    for (String currentArg : args) {
      if (currentArg.equals(OPTION_PRINT) || currentArg.equals(OPTION_README)) {
        if (nonOptionArgCount > 0) {
          throw new ParseException("Invalid argument: \"" + currentArg + "\" optional argument must precede required arguments", -1);
        }
        if (!options.contains(currentArg)) {
          options.add(currentArg);
        } else {
          throw new ParseException("Invalid argument: \"" + currentArg + "\" cannot be duplicated", -1);
        }
      }
      else if (nonOptionArgCount <= 0 && currentArg.startsWith("-")) {
        throw new ParseException("Invalid argument: \"" + currentArg + "\" option not recognized", -1);
      }
      else {
        ++nonOptionArgCount;
      }
    }
    return options;
  }

  public static boolean isValidAirportCode(String code) {
    return code.length() == 3? true : false;
  }

  public static void printUsageMessageError(String precedingMessage) {
    if (precedingMessage != null && !precedingMessage.equals("")) {
      System.err.println(precedingMessage + "\n" + VERBOSE_USAGE);
    } else {
      System.err.println(VERBOSE_USAGE);
    }
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

  /**
   * This method creates a customized number of spaces used for tab spaces using
   * a StringBuilder object
   * @param numOfSpaces The number of spaces to concatenate
   * @return The space String object from the tabs object
   */
  public static String tabulate(int numOfSpaces) {
    StringBuilder tabs = new StringBuilder();
    for (int i = 0; i < numOfSpaces; ++i) {
      tabs.append(" ");
    }
    return tabs.toString();
  }

  /**
   * This method attempts to parse the date section of the args passed in. The criteria for the date and time format
   * is that it must be in this format: mm/dd/yyyy hh:mm
   * This method uses the SimpleDateFormat class to parse and format the date/time argument.
   * @param dateTimeArg The argument that contains the date and time string
   * @return The formatted string of the date and time argument.
   *  The null value is returned if there is no argument for the date and time. An error string message is
   *  returned if either the date argum     printUsageMessageError();
      System.exit(1);ent part of the argument is not the right format or not a valid date, or
   *  if the time is not a valid 24-hour time format.
   */
  public static String formatDateTime(String dateTimeArg) throws ParseException {
    StringBuilder resultingStr = new StringBuilder();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    dateFormat.setLenient(false); //to disallow dates like 03/33/2014, etc
    Date formattedDate;
    try {
      formattedDate = dateFormat.parse(dateTimeArg);
      resultingStr.append(dateFormat.format(formattedDate));
    } catch (ParseException e) {
      throw new ParseException("Invalid argument: Please enter a valid date (mm/dd/yyyy format) and/or time (24-hour time format)", -1);
    }
    return resultingStr.toString();
  }

  /**
   * This method prints the hard-coded README to standard out, showing a brief description of the what this program does
   */
  public static void printReadme() {
    final String HEADER = "--------------------------A I R L I N E   F L I G H T   I N F O   P R O G R A M---------------------------------------";
    final String README = "This program prompts the user for an arline flight information, and allows the user to print " +
      "out details about\nan airline's flight information. There are six (6) arguments required for the airline flight info in " +
      "this exact order:\n\t<name> <flight number> <airport source> <departure date/time> " +
      "<aiport destination> <arrival date/time>\nThe date and time arguments must be in mm/dd/yyyy hh:mm format "  +
      "and the time portion is in the 24-hour format.\nSee usage details below.\n\n[EXAMPLE]\nHere is a complete command line usage example with the " +
      "printing option:\n\tjava edu.pdx.cs410J.laurente.Project1 -print \"Hawaiian Airlines\" 234 PDX 03/02/2014 04:53 HNL 03/02/2014 21:53\nThe output " +
      "for this would be:\n\tHawaiian Airlines with 1 flight: Flight 234 departs PDX at 03/02/2014 04:53 arrives HNL at 03/02/2014 21:53\n";
    System.out.println("----------------------------------------------------------------------------------------------------------------------");
    System.out.println(HEADER);
    System.out.println("----------------------------------------------------------------------------------------------------------------------");
    System.out.println(README);
    System.out.println("----------------------------------------------------------------------------------------------------------------------");
    System.out.println(VERBOSE_USAGE);
  }
}