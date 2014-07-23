package edu.pdx.cs410J.laurente;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import edu.pdx.cs410J.InvokeMainTestCase;
import static junit.framework.Assert.assertEquals;

/**
 * Tests the functionality in the {@link Project3} main class.
 */
public class Project3Test extends InvokeMainTestCase {

    /**
     * Invokes the main method of {@link Project3} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
      return invokeMain( Project3.class, args );
    }

  /**
   * Tests that invoking the main method with no arguments issues an error
   */
  @Test
  public void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertEquals(new Integer(1), result.getExitCode());
    assertTrue(result.getErr().contains("Missing command line arguments"));
  }

  @Ignore
  @Test
  public void checkUsagePrinting() {
    MainMethodResult result = invokeMain();
    System.out.print(result.getErr());
  }

  @Ignore
  @Test
  public void testDateTimeFormatPrinting() {
    MainMethodResult result = invokeMain("Hawaiian Airlines", "234", "PDX", "3/02/2014", "4:53", "HNL", "3/02/2014", "21:53");
    assertEquals("03/02/2014 04:53", result.getOut());
  }

  @Ignore
  @Test
  public void testPrintOption() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDX", "3/02/2014", "4:53", "HNL", "3/02/2014", "21:53");
    System.out.println(result.getOut());
    //System.out.println(result.getExitCode());
  }

  @Test
  public void testInvalidFlightNumberAndPrintError() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "twothreefour", "PDX", "3/02/2014", "4:53", "HNL", "3/02/2014", "21:53");
    assertTrue(result.getErr().contains("Invalid argument: flight number must be a valid integer"));
  }

  @Test
  public void printErrWhenPrintArgWithNotEnoughRequiredArgs() {
    MainMethodResult result = invokeMain("-print", "twothreefour", "PDX", "3/02/2014", "4:53", "HNL", "3/02/2014", "21:53");
    assertTrue(result.getErr().contains("Insufficient number of arguments: Not enough information about the flight was given"));
  }

  @Test
  public void testInvalidSourceAirportCode() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDXz", "3/02/2014", "4:53", "HNL", "3/02/2014", "21:53");
    assertTrue(result.getErr().contains("Error: the source airport code \"PDXZ\" is not a valid code"));
  }

  @Test
  public void testInvalidDestAirportCode() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDX", "3/02/2014", "4:53", "HNLz", "3/02/2014", "21:53");
    assertTrue(result.getErr().contains("Error: the destination airport code \"HNLZ\" is not a valid code"));
  }

  @Ignore
  @Test
  public void testPrintOptionWithValidArgs() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDX", "3/2/2014", "4:53", "HNL", "3/2/2014", "21:53");
    assertTrue(result.getOut().contains("Hawaiian Airlines with 1 flight: Flight 234 departs PDX at 03/02/2014 04:53 arrives HNL at 03/02/2014 21:53"));
  }

  @Test
  public void testReadmeOption() {
    MainMethodResult result = invokeMain("-print", "-README","Hawaiian Airlines", "234", "PDX", "3/2/2014 4:53", "HNL", "3/2/2014 21:53");
    System.out.println(result.getOut());
    assertTrue(result.getOut().contains("This program prompts the user for an arline flight information"));

  }

  @Test
  public void testTextFileOptionWithErrOutput() {
    MainMethodResult result = invokeMain("-print", "-textFile", "testFile", "Hawaiian", "10", "PDX", "3/2/2014", "4:53", "HNL", "3/2/2014", "21:53");
    System.out.println(result.getErr());
  }

  @Test
  public void testTextFileOptionWithOutOutput() {
    MainMethodResult result = invokeMain("-print", "-textFile", "testFile", "Hawaiian", "10", "PDX", "3/2/2014", "4:53", "HNL", "3/2/2014", "21:53");
    System.out.println(result.getOut());
  }

  @Test
  public void extraneousArgs() {
    MainMethodResult result = invokeMain("-print", "-textFile", "laurente/laurente-x.txt", "Test6", "123", "PDX", "03/03/2014", "12:00", "ORD", "01/04/2014", "16:00", "fred");
    assertTrue(result.getErr().contains("Extraneous argument(s) encountered: only eight (8) valid arguments is required"));

  }

  @Test
  public void testDateTimeWithAmPm() {
    MainMethodResult result = invokeMain("-print", "-textFile", "laurente/test.txt", "Hawaiian", "10", "PDX", "3/2/2014", "4:53", "pm", "HNL", "3/3/2014", "1:00", "am");
    System.out.println(result.getOut());
    //assertTrue(result.getOut().contains("Hawaiian with 1 flight: Flight 10 departs PDX at Sun Mar 02 16:53:00 PST 2014 arrives HNL at Sun Mar 02 09:53:00 PST 2014"));
  }

  @Test
  public void testInvalidAiportCode() {
    MainMethodResult result = invokeMain("-print", "Hawaiian", "10", "PDL", "3/2/2014", "4:53", "pm", "HNL", "3/3/2014", "1:00", "am");
    System.out.println(result.getErr());
    assertTrue(result.getErr().contains("Invalid argument: the airport \"PDL\" does not exist"));
  }
}