package edu.pdx.cs410J.laurente;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;

/**
* The main class that parses the command line and communicates with the
* Airline server using REST.
*/
public class Project4 {
  public static final String USAGE_NAME = "name" + tabulate(18) + "The name of the airline";
  public static final String USAGE_FLIGHTNUMBER = "flightNumber" + tabulate(10) + "The flight number";
  public static final String USAGE_SRC = "src" + tabulate(19) + "Three-letter code of departure airport";
  public static final String USAGE_DEPARTTIME = "departTime" + tabulate(12) + "Departure date time am/pm";
  public static final String USAGE_DEST = "dest" + tabulate(18) + "Three-letter code of arrival airport";
  public static final String USAGE_ARRIVAL = "arrivalTime" + tabulate(11) + "Arrival date time am/pm";
  public static final String USAGE_PRETTY = "-pretty file" + tabulate(10) + "Pretty print the airline's flights to\n" +
    tabulate(26) + "a text file or standard out (file -)";
  public static final String USAGE_TEXTFILE = "-textFile file" + tabulate(8) + "Where to read/write the airline info";
  public static final String USAGE_PRINT = "-print" + tabulate(16) + "Prints a description of the new flight";
  public static final String USAGE_README = "-README" + tabulate(15) + "Prints a README for this project and exits";
  public static final String OPTION_PRETTYPRINT = "-pretty";
  public static final String OPTION_TEXTFILE = "-textFile";
  public static final String OPTION_PRINT = "-print";
  public static final String OPTION_README = "-README";
  public static final String VERBOSE_USAGE = buildUsageString();
  public static final int MAX_NUMBER_OF_ARGS = 10;
  public static final String ABSOLUTE_PATH = System.getProperty("user.dir");

  public static final String MISSING_ARGS = "Missing command line arguments";

  public static void main(String... args) {
    String hostName = null;
    String portString = null;
    String key = null;
    String value = null;

    for (String arg : args) {
      if (hostName == null) {
        hostName = arg;

      } else if ( portString == null) {
        portString = arg;

      } else if (key == null) {
        key = arg;

      } else if (value == null) {
        value = arg;

      } else {
        usage("Extraneous command line argument: " + arg);
      }
    }

    if (hostName == null) {
        usage( MISSING_ARGS );

    } else if ( portString == null) {
        usage( "Missing port" );
    }

    int port;
    try {
        port = Integer.parseInt( portString );

    } catch (NumberFormatException ex) {
        usage("Port \"" + portString + "\" must be an integer");
        return;
    }

    AirlineRestClient client = new AirlineRestClient(hostName, port);

    HttpRequestHelper.Response response;
    try {
      if (key == null) {
        // Print all key/value pairs
        response = client.getAllKeysAndValues();

      } else if (value == null) {
        // Print all values of key
        response = client.getValues(key);

      } else {
        // Post the key/value pair
        response = client.addKeyValuePair(key, value);
      }

      checkResponseCode( HttpURLConnection.HTTP_OK, response);

    } catch ( IOException ex ) {
      error("While contacting server: " + ex);
      return;
    }

    System.out.println(response.getContent());

    System.exit(0);
  }

  /**
   * Makes sure that the give response has the expected HTTP status code
   * @param code The expected status code
   * @param response The response from the server
   */
  private static void checkResponseCode( int code, HttpRequestHelper.Response response )
  {
    if (response.getCode() != code) {
      error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                          response.getCode(), response.getContent()));
    }
  }

  private static void error( String message )
  {
    PrintStream err = System.err;
    err.println("** " + message);

    System.exit(1);
  }

  /**
   * Prints usage information for this program and exits
   * @param message An error message to print
   */
  private static void usage( String message )
  {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("usage: java Project4 host port [key] [value]");
    err.println("  host    Host of web server");
    err.println("  port    Port of web server");
    err.println("  key     Key to query");
    err.println("  value   Value to add to server");
    err.println();
    err.println("This simple program posts key/value pairs to the server");
    err.println("If no value is specified, then all values are printed");
    err.println("If no key is specified, all key/value pairs are printed");
    err.println();

    System.exit(1);
  }



  /**
   * This method retrieves the pretty file name from the options arguments list
   * @param options   The list of strings containing the options prefix and their suffix from the argument array args
   * @return          The file name to be pretty-printed to, or null if the destination is standard out
   */
  private static String getPrettyPrintFileName(List<String> options) {
    String temp = getFileName(options, OPTION_PRETTYPRINT);
    if (temp == null || (temp.startsWith("-")) && !temp.equals("-")) {
      printUsageMessageErrorAndExit("Invalid argument: \"" + temp + "\" is not a valid -pretty argument");
    }
    return temp.equals("-") ? null : temp; //null if "-" is suffix arguments
  }

  /**
   * This method writes an airline's flight data to a file or standard out in 'pretty' format using the PrettyPrint class
   * @param prettyDumper          The PrettyPrint class object used to dump data to a file or standard out
   * @param prettyPrintFileName   The file name that refers to the output file. If null, then output is standard out
   * @param airline               The airline object to be written about
   */
  private static void prettyPrinter(PrettyPrint prettyDumper, String prettyPrintFileName, Airline airline) {
    if (prettyPrintFileName != null) { //if -pretty argument is followed by a file name
      File file = null;
      try {
        file = new File(prettyPrintFileName);
        if (!file.exists()) {
          if (file.createNewFile() == false) {
            printUsageMessageErrorAndExit("File error: encountered a problem when creating a the file " + prettyPrintFileName);
          }
        }
        prettyDumper = new PrettyPrint(file); //print to a that file
      } catch (Exception e) {
        if (e instanceof ParserException) {
          printUsageMessageErrorAndExit(e.getMessage());
        } else if (e instanceof IOException) {
          printUsageMessageErrorAndExit("File error: " + e.getMessage());
        }
      }
      try {
        prettyDumper.dump(airline);
      } catch (IOException e) {
        printUsageMessageErrorAndExit("File error: writing to " + prettyDumper.getFileName() + " was unsuccessful\n\t" + e.getMessage());
      }
    }
    else { //else -pretty argument is followed by a '-'
      prettyDumper = new PrettyPrint(new PrintWriter(System.out));
      try {
        prettyDumper.dump(airline); //print to standard out
      } catch (IOException e) {
        printUsageMessageErrorAndExit("File error: writing to " + prettyDumper.getFileName() + " was unsuccessful\n\t" + e.getMessage());
      }
    }
  }

  /**
   * This method writes an airline's list of flights information to a file
   * @param anAirline   The airline to be written to a file about
   * @param dumper      The TextDumper object that writes dumps the airline's data to a file
   */
  private static void writeAirlineFlightInfo(Airline anAirline, TextDumper dumper) {
    try {
      dumper.dump(anAirline);
    } catch (IOException e) {
      printUsageMessageErrorAndExit("File error: writing to " + dumper.getFileName() + " was unsuccessful\n\t" + e.getMessage());
    }
  }

  /**
   * This method retrieves the file name to read from and written to, extracted from the options list
   * @param options   The list of strings containing the options prefix and their suffix from the argument array args
   * @return          The file name provided by the user
   */
  private static String getFileName(List<String> options, String optionType) {
    String fileFromArgs;
    StringBuilder absoluteFilePath = null;
    int prefixIndex = options.indexOf(optionType);
    fileFromArgs = prefixIndex + 1 <= options.size() - 1 ? options.get(prefixIndex + 1) : null;
    if (fileFromArgs == null) {
      printUsageMessageErrorAndExit("File error: Could not retrieve file name from command line arguments");
    }
    else if (fileFromArgs.contains("/")){ //ignore parent folders
      String[] pathList = fileFromArgs.split("/");
      int fileNameIndex = pathList.length - 1;
      fileFromArgs = pathList[fileNameIndex];
      //System.err.println("Path object: " + Paths.get(fileFromArgs).toString());
      absoluteFilePath = new StringBuilder(ABSOLUTE_PATH).append("/").append(fileFromArgs);
    }
    return absoluteFilePath == null ? fileFromArgs : absoluteFilePath.toString();
  }

  /**
   * This method gets the current flight's departure date and time
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The current flight's departure date and time as a String object
   */
  private static Date getDateAndTimeArrival(String[] args, int argStartingPosition) {
    String arrivalDate;
    String arrivalTime;
    String amPm;
    Date arrival;
    arrivalDate = args[argStartingPosition + 7];
    arrivalTime = args[argStartingPosition + 8];
    amPm = args[argStartingPosition + 9];
    try {
      arrival = formatDateTime(arrivalDate + " " + arrivalTime + " " + amPm);
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
    dest = args[argStartingPosition + 6].toUpperCase(); //use upper-case format
    if (!isValidAirportCode(dest) || dest.equals("")) {
      printUsageMessageErrorAndExit("Invalid argument: the destination airport code \"" + dest + "\" is not a valid code");
    }
    else if (AirportNames.getName(dest) == null) {
      printUsageMessageErrorAndExit("Invalid argument: the airport \"" + dest + "\" does not exist");
    }
    return dest;
  }

  /**
   * This method gets the current flight's departure date and time
   * @param args                  The incoming command line arguments
   * @param argStartingPosition   The offset index used as the base index
   * @return                      The current flight's departure date and time as a String object
   */
  private static Date getDateAndTimeDateDeparture(String[] args, int argStartingPosition) {
    String departDate;
    String departTime;
    Date departure;
    String amPm;
    departDate = args[argStartingPosition + 3];
    departTime = args[argStartingPosition + 4];
    amPm = args[argStartingPosition + 5];
    try {
      departure = formatDateTime(departDate + " " + departTime + " " + amPm);
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
      printUsageMessageErrorAndExit("Invalid argument: the source airport code \"" + src + "\" is not a valid code");
    }
    else if (AirportNames.getName(src) == null) {
      printUsageMessageErrorAndExit("Invalid argument: the airport \"" + src + "\" does not exist");
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
      int size = anAirline.getFlights().size();
      if (size == 1) {
        System.out.println(anAirline.toString() + ": " + anAirline.getFlights().toArray()[0].toString());
      }
      else if (size > 1) {
        List<Object> listFlights = Arrays.asList((anAirline.getFlights()).toArray());
        StringBuilder output = new StringBuilder();
        output.append(anAirline.toString()).append(":\n");
        for (int i = 0; i < size; ++i) {
          Flight flight = (Flight)listFlights.get(i);
          output.append("\t").append(flight.toString()).append("\n");
        }
        System.out.print(output);
      }
    } else {
      printUsageMessageErrorAndExit("Error: cannot print information about an empty airline");
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
    boolean getSuffix = false; //used to determine if the current argument is a suffix to a command
    for (String currentArg : args) {
      if (getSuffix == true) {
        options.add(currentArg);
        getSuffix = false;
      }
      else if (currentArg.equals(OPTION_PRINT) || currentArg.equals(OPTION_README) ||
        currentArg.equals(OPTION_TEXTFILE) || currentArg.equals(OPTION_PRETTYPRINT)) {
        if (nonOptionArgCount > 0) {
          throw new ParseException("Invalid argument: \"" + currentArg + "\" optional argument must precede required arguments", -1);
        }
        if (!options.contains(currentArg) || getSuffix == true) {
          options.add(currentArg);
          if (currentArg.equals(OPTION_TEXTFILE) || currentArg.equals(OPTION_PRETTYPRINT)) {
            getSuffix = true; //signal next iteration that next argument is part of the suffix the to the prefix argument
          }
        } else {
          throw new ParseException("Invalid argument: \"" + currentArg + "\" cannot be duplicated", -1);
        }
      }
      else if (currentArg.startsWith("-") && !currentArg.equals(OPTION_PRETTYPRINT)) {
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
    usage.append("\nusage: java edu.pdx.cs410J.laurente.Project3 [options] <args>\n");
    usage.append("  args are (in this order):\n");
    usage.append("    ").append(USAGE_NAME).append("\n");
    usage.append("    ").append(USAGE_FLIGHTNUMBER).append("\n");
    usage.append("    ").append(USAGE_SRC).append("\n");
    usage.append("    ").append(USAGE_DEPARTTIME).append("\n");
    usage.append("    ").append(USAGE_DEST).append("\n");
    usage.append("    ").append(USAGE_ARRIVAL).append("\n");
    usage.append("  options are (options may appear in any order):\n");
    usage.append("    ").append(USAGE_PRETTY).append("\n");
    usage.append("    ").append(USAGE_TEXTFILE).append("\n");
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
   * is that it must be in this format: mm/dd/yyyy hh:mm am|pm
   * This method uses the SimpleDateFormat class to parse and format the date/time argument.
   * @param dateTimeArg         The argument that contains the date and time string
   * @return                    The formatted string of the date and time argument.
   * @exception ParseException  An error is thrown if dateTimeArg is not of the form "MM/dd/yyy h:mm a"
   */
  private static Date formatDateTime(String dateTimeArg) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
    dateFormat.setLenient(false); //to disallow dates like 03/33/2014, etc
    Date formattedDate;
    try {
      formattedDate = dateFormat.parse(dateTimeArg);
      //resultingStr.append(dateFormat.format(formattedDate));
    } catch (ParseException e) {
      throw new ParseException("date/time arguments must be in this format ('a' is am/pm marker): mm/dd/yyyy hh/mm a", -1);
    }
    return formattedDate;
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
      "for this would be:\n\tHawaiian Airlines with 1 flight: Flight 234 departs PDX at 03/02/2014 04:53 arrives HNL at 03/02/2014 21:53\n" +
      "\nFILES\nThe format for writing and reading airline data from a file is:\n" +
      "<airline name> <number of flights> flight(s):\n    <flight1 flight number> <source> <depart date> <depart time> <dest> <arrive date> <arrive time>\n" +
      "    <flight2 flight number> <source> <depart date> <depart time> <dest> <arrive date> <arrive time>\n    etc...<end of file>" +
      "\nNote: if a file does not exist, it will be created and written to. If the file exists and is empty, it will be overwritten";
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    System.out.println(HEADER);
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    System.out.println(README);
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    System.out.println(VERBOSE_USAGE);
  }
}