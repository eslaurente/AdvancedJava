package edu.pdx.cs410J.laurente;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The main class for the CS410J airline Project
 */
public class Project2 {
  public static final String USAGE_NAME = "name" + tabulate(18) + "The name of the airline";
  public static final String USAGE_FLIGHTNUMBER = "flightNumber" + tabulate(10) + "The flight number";
  public static final String USAGE_SRC = "src" + tabulate(19) + "Three-letter code of departure airport";
  public static final String USAGE_DEPARTTIME = "departTime" + tabulate(12) + "Departure date and time (24-hour time)";
  public static final String USAGE_DEST = "dest" + tabulate(18) + "Three-letter code of arrival airport";
  public static final String USAGE_ARRIVAL = "arrivalTime" + tabulate(11) + "Arrival date and time (24-hour time)";
  public static final String USAGE_PRINT = "-print" + tabulate(16) + "Prints a description of the new flight";
  public static final String USAGE_README = "-README" + tabulate(15) + "Prints a README for this project and exits";
  public static final String OPTION_PRINT = "-print";
  public static final String OPTION_README = "-README";
  public static final String VERBOSE_USAGE = buildUsageString();

  /**
   *  The main method
   * @param args    The command line arguments used for parsing for the airline and flight information
   */
  public static void main(String[] args) {
    Airline anAirline = null;
    String name, flightNumber, src, departDate, departTime, departure, dest, arrivalDate, arrivalTime, arrival;
    int argStartingPosition = 0; //Actual starting index offset by the number of options
    List<String> options;
    //Class c = AbstractAirline.class;  // Refer to one of Dave's classes so that we can be sure it is on the classpath
    //System.err.print("Missing command line arguments");
    if (args.length == 0) {
      printUsageMessageErrorAndExit("Missing command line arguments");
    }
    //Parse option arguments and collect them.
    try {
      options = getOptions(args);
      argStartingPosition = options.size(); //offset the rest of arguments starting index
    } catch (ParseException e) {
      printUsageMessageErrorAndExit(e.getMessage());
      throw new AssertionError("Unreachable statement reached.");
    }
    if (options.contains(OPTION_README)) { //since -README has high precedence, display it ignore parsing
      printReadme();
      System.exit(0); //terminate immediately
    }
    else if (args.length - argStartingPosition < 8) {
      printUsageMessageErrorAndExit("Insufficient number of arguments: Not enough information about the flight was given");
    }
    else if (args.length - argStartingPosition > 8) {
      printUsageMessageErrorAndExit("Extraneous argument(s) encountered: only eight (8) valid arguments is required");
    }

    //--Begin parsing the rest of the arguments --
    //Get name
    name = getAirlineName(args, argStartingPosition);
    //Get flight number
    flightNumber = getFlightNumber(args, argStartingPosition);
    //Get the source airport code
    src = getSourceAirportCode(args, argStartingPosition);
    //Get departure date and time
    departure = getDateAndTimeDateDeparture(args, argStartingPosition);
    //Get destination airport code
    dest = getDestAirportCode(args, argStartingPosition);
    //Get arrival date and time
    arrival = getDateAndTimeArrival(args, argStartingPosition);
    //Create airline object
    anAirline = new Airline(name, new Flight(flightNumber, src, departure, dest, arrival));
    if (anAirline == null) {
      printUsageMessageErrorAndExit("Error: Something serious went wrong");
    }
    if (options.contains(OPTION_PRINT)) { //Print airline info to standard out if there is -print
      printAirlineFlightInfo(anAirline);
    }
    System.exit(0);
  }

  /**
   * This method gets the current flight's departure date and time
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The current flight's departure date and time as a String object
   */
  private static String getDateAndTimeArrival(String[] args, int argStartingPosition) {
    String arrivalDate;
    String arrivalTime;
    String arrival;
    arrivalDate = args[argStartingPosition + 6];
    arrivalTime = args[argStartingPosition + 7];
    try {
      arrival = formatDateTime(arrivalDate + " " + arrivalTime);
    } catch (ParseException e) {
      printUsageMessageErrorAndExit("Invalid argument: For the flight's arrival, " + e.getMessage());
      throw new AssertionError("Unreachable statement reached.");
    }
    return arrival;
  }

  /**
   * This method gets the current flight's airport destination three-letter code name
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The current flight's airport destination three-letter code String object
   */
  private static String getDestAirportCode(String[] args, int argStartingPosition) {
    String dest;
    dest = args[argStartingPosition + 5].toUpperCase(); //use upper-case format
    if (!isValidAirportCode(dest) || dest.equals("")) {
      printUsageMessageErrorAndExit("Error: the destination airport code \"" + dest + "\" is not a valid code");
    }
    return dest;
  }

  /**
   * This method gets the current flight's departure date and time
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The current flight's departure date and time as a String object
   */
  private static String getDateAndTimeDateDeparture(String[] args, int argStartingPosition) {
    String departDate;
    String departTime;
    String departure;
    departDate = args[argStartingPosition + 3];
    departTime = args[argStartingPosition + 4];
    try {
      departure = formatDateTime(departDate + " " + departTime);
    } catch (ParseException e) {
      printUsageMessageErrorAndExit("Invalid argument: For the flight's depature, " + e.getMessage());
      throw new AssertionError("Unreachable statement reached.");
    }
    return departure;
  }

  /**
   * This method gets the current flight's airport source code three-letter name
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The current flight's airport source code
   */
  private static String getSourceAirportCode(String[] args, int argStartingPosition) {
    String src;
    src = args[argStartingPosition + 2].toUpperCase(); //use upper-case format
    if (!isValidAirportCode(src) || src.equals("")) {
      printUsageMessageErrorAndExit("Error: the source airport code \"" + src + "\" is not a valid code");
    }
    return src;
  }

  /**
   * This method gets the current flight's flight number
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The flight number of the current flight as a String object
   */
  private static String getFlightNumber(String[] args, int argStartingPosition) {
    String flightNumber;
    try {
      flightNumber = args[argStartingPosition + 1];
      int unused = Integer.parseInt(flightNumber);
    } catch (NumberFormatException e) {
      printUsageMessageErrorAndExit("Invalid argument: flight number must be a valid integer");
      throw new AssertionError("Unreachable statement reached.");

    }
    return flightNumber;
  }

  /**
   * This method checks the 'name' portion of args and parses the airline's name
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The name of the airline
   */
  private static String getAirlineName(String[] args, int argStartingPosition) {
    String name;
    name = args[argStartingPosition];
    if (name.equals("")) {
      printUsageMessageErrorAndExit("Invalid argument: Airline name cannot be empty");
    }
    return name;
  }

  /**
   * This method will print to standard out the string representation of an Airline object. This version
   * of the program only prints out the first and only flight that an airline has.
   * @param anAirline   The Airline object to be printed
   */
  private static void printAirlineFlightInfo(Airline anAirline) {
    if (anAirline != null) {
      System.out.println(anAirline.toString() + ": " + anAirline.getFlights().toArray()[0].toString());
    }
  }

  /**
   * This method parses the optional portion of the arguments
   * @param args              The full arguments to be parsed
   * @return                  The List of String that contains the optional arguments found from args
   * @throws ParseException   Throws this exception if: there is a duplicate of the option; an argument with a
   *                          suffix of '-' is not valid optional argument; or an option is found after a non-optional
   *                          argument is encountered.
   */
  private static List<String> getOptions(String[] args) throws ParseException{
    List<String> options = new ArrayList<String>();
    int nonOptionArgCount = 0;
    //boolean getSuffix = false;
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
      else if (currentArg.startsWith("-")) {
        throw new ParseException("Invalid argument: \"" + currentArg + "\" option not recognized", -1);
      }
      else {
        ++nonOptionArgCount;
      }
    }
    return options;
  }

  /**
   * This method checks to see if an airport code has exactly 3 characters representing a valid airport code
   * @param code    The airport code to be checked
   * @return        The boolean value: true if <code>code</code> is a valid airport code, false if not
   */
  private static boolean isValidAirportCode(String code) {
    return code.length() == 3? true : false;
  }

  /**
   * This method prints to the system's error stream the detailed usage information of the program then exits
   * the program with and exit code of 1. If the there is a precedingMessage string, it will print that first and
   * then the usage information.
   * @param precedingMessage    The message to print first before the usage information
   */
  private static void printUsageMessageErrorAndExit(String precedingMessage) {
    if (precedingMessage != null && !precedingMessage.equals("")) {
      System.err.println(precedingMessage + "\n" + VERBOSE_USAGE);
    } else {
      System.err.println(VERBOSE_USAGE);
    }
    System.exit(1);
  }


  /**
   * This method uses StringBuilder to build the program's usage information string
   * @return    The program's usage information String
   */
  private static String buildUsageString() {
    StringBuilder usage = new StringBuilder();
    usage.append("\nusage: java edu.pdx.cs410J.laurente.Project1 [options] <args>\n");
    usage.append("  args are (in this order):\n");
    usage.append("    ").append(USAGE_NAME).append("\n");
    usage.append("    ").append(USAGE_FLIGHTNUMBER).append("\n");
    usage.append("    ").append(USAGE_SRC).append("\n");
    usage.append("    ").append(USAGE_DEPARTTIME).append("\n");
    usage.append("    ").append(USAGE_DEST).append("\n");
    usage.append("    ").append(USAGE_ARRIVAL).append("\n");
    usage.append("  options are (options may appear in any order):\n");
    usage.append("    ").append(USAGE_PRINT).append("\n");
    usage.append("    ").append(USAGE_README);
    return usage.toString();
  }

  /**
   * This method creates a customized number of spaces used for tab spacing, using int parameter to determine
   * how many spaces to append.
   * @param numOfSpaces The number of spaces to concatenate
   * @return The space String object from the tabs object
   */
  private static String tabulate(int numOfSpaces) {
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
   * @param dateTimeArg         The argument that contains the date and time string
   * @return                    The formatted string of the date and time argument.
   * @exception ParseException  An error is thrown if dateTimeArg is not of the form "MM/dd/yyy HH:mm"
   */
  private static String formatDateTime(String dateTimeArg) throws ParseException {
    StringBuilder resultingStr = new StringBuilder();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm"); //capital HH means to use 24-hour format
    dateFormat.setLenient(false); //to disallow dates like 03/33/2014, etc
    Date formattedDate;
    try {
      formattedDate = dateFormat.parse(dateTimeArg);
      resultingStr.append(dateFormat.format(formattedDate));
    } catch (ParseException e) {
      throw new ParseException("date/time arguments must be in this format (24-hour time): mm/dd/yyyy hh/mm", -1);
    }
    return resultingStr.toString();
  }

  /**
   * This method prints the hard-coded README to standard out, showing a brief description of the what this program does
   */
  public static void printReadme() {
    final String HEADER = "-------------------------------A I R L I N E   F L I G H T   I N F O   P R O G R A M-----------------------------------------";
    final String README = "This program prompts the user for an arline flight information, and allows the user to print " +
      "out details about\nan airline's flight information. There are eight (8) arguments required for the airline flight info in " +
      "this exact order:\n  <airline name> <flight number> <airport src> <depart date> <depart time> " +
      "<airport dest> <arrive date> <arrive time>\nThe date and time arguments must be in mm/dd/yyyy hh:mm format "  +
      "and the time portion is in the 24-hour format.\nSee usage details below.\n\n[EXAMPLE]\nHere is a complete command line usage example with the " +
      "printing option:\n\tjava edu.pdx.cs410J.laurente.Project1 -print \"Hawaiian Airlines\" 234 PDX 03/02/2014 04:53 HNL 03/02/2014 21:53\nThe output " +
      "for this would be:\n\tHawaiian Airlines with 1 flight: Flight 234 departs PDX at 03/02/2014 04:53 arrives HNL at 03/02/2014 21:53\n";
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    System.out.println(HEADER);
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    System.out.println(README);
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    System.out.println(VERBOSE_USAGE);
  }
}