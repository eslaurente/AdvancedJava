package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import jdk.nashorn.internal.objects.NativeString;

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
   * Where each character must be an integer and can have only one or two digits. Any other combination of sequence of
   * characters will be invalid and will result in this method returning a null value.
   * @param dateTimeArg The incoming
   * @return The resulting formatted date and time String object. null value returned if argument is in an invalid format
   */
  public static String formatDateTime(String dateTimeArg) {
    StringBuilder resultingStr = null;
    String[] splitDateTime = null; //Result must be an array of 2 String elements
    String[] splitDate = null; //Result must be an array of 3 String elements
    String[] splitTime = null; //Result must be an array of 2 String elements

    if (dateTimeArg == null || dateTimeArg.equals("")) {
      return null;
    }
    else {
      splitDateTime = dateTimeArg.split("\\s"); //Should result into an array of two Strings:
                                             // first element -- the date, second element -- the time
      if (splitDateTime.length != 2) {
        return null; // If return null for invalid format there is no space between date and time
      }
      String date = splitDateTime[0];
      String time = splitDateTime[1];
      splitDate = date.split("/");
      splitTime = time.split(":");
      if (splitDate.length != 3 || splitTime.length != 2) {
        return null;  // Return null for formatting error if the
      }
      else {
        //Build the date part of the resulting string
        for (String subStr : splitDate) {
          if (subStr.matches("\\d") || subStr.matches("\\d\\d")) {
            try { //substring must pass Integer parsing to be a valid argument
              resultingStr.append(Integer.parseInt(subStr));
            }
            catch (NumberFormatException e){
              return null; //Return null for invalid format if a date number is not an integer
            }
            resultingStr.append("/");
          }
          else {
            return null; //Return null for invalid format if the date number has more than two numbers
          }
        }
        resultingStr.append(" ");
        //Build the time part of the resulting String
        for (String subStr : splitTime) {
          if (subStr.matches("\\d") || subStr.matches("\\d\\d")) {
            try { //substring must pass Integer parsing to be a valid argument
              resultingStr.append(Integer.parseInt(subStr));
            }
            catch (NumberFormatException e){
              return null; //Return null for invalid format if a time number is not an integer
            }
          }
          else {
            return null; //Return null for invalid format if the time number has more than two numbers
          }
          resultingStr.append(":");
        }
      }
    }
    return resultingStr.toString();
  }
}