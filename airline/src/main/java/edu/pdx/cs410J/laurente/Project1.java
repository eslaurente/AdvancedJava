package edu.pdx.cs410J.laurente;

import edu.pdx.cs410J.AbstractAirline;
import jdk.nashorn.internal.objects.NativeString;
import sun.plugin2.applet.Applet2ThreadGroup;

import javax.lang.model.util.SimpleElementVisitor6;
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
  public static final String USAGE_ARRIVAL = "arrival" + tabulate(21) + "Arrival date and time (24-hour time)";
  public static final String USAGE_PRINT = "-print" + tabulate(22) + "Prints a description of the new flight";
  public static final String USAGE_README = "-README" + tabulate(21) + "Prints a README for this project and exits";
  public static final String OPTION_PRINT = "-print";
  public static final String OPTION_README = "-README";

  public static void main(String[] args) {
    int argStartingPosition = 0;
    List<String> options;
    Class c = AbstractAirline.class;  // Refer to one of Dave's classes so that we can be sure it is on the classpath
    System.err.print("Missing command line arguments");

    try {
      options = getOptions(args);
      argStartingPosition = options.size();
    } catch (ParseException e) {
      printUsage();
      System.exit(1);
    }
    //Validate
    String formattingResult = formatDateTime(args[argStartingPosition + 3]);
    if(formattingResult == null) {
      printUsage();
      System.exit(1);
    }
    else {
      System.out.print(formattingResult);
    }
    System.exit(0);
  }

  public static List<String> getOptions(String[] args) throws ParseException{
    List<String> options = new ArrayList<String>();
    int nonOptionArgCount = 0;
    for (String currentArg : args) {
      if (currentArg.equals(OPTION_PRINT) || currentArg.equals(OPTION_README)) {
        if (nonOptionArgCount > 1) {
          throw new ParseException("", -1);
        }
        options.add(currentArg);
      }
      else {
        ++nonOptionArgCount;
      }
    }
    return options;
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
   * This method uses the SimpleDateFormat class to parse and format the date/time argument.
   * @param dateTimeArg The argument that contains the date and time string
   * @return The formatted string of the date and time argument.
   *  The null value is returned if there is no argument for the date and time. An error string message is
   *  returned if either the date argument part of the argument is not the right format or not a valid date, or
   *  if the time is not a valid 24-hour time format.
   */
  public static String formatDateTime(String dateTimeArg) {
    StringBuilder resultingStr = new StringBuilder();
    if (dateTimeArg == null || dateTimeArg.equals("")) {
      return null;
    } else {
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
      dateFormat.setLenient(false);
      Date formattedDate;
      try {
        formattedDate = dateFormat.parse(dateTimeArg);
        resultingStr.append(dateFormat.format(formattedDate));
      } catch (ParseException e) {
        return "Invalid argument: Please enter a valid date (mm/dd/yyyy format) and/or time (24-hour time format)";
      }
    }
    return resultingStr.toString();
  }
}