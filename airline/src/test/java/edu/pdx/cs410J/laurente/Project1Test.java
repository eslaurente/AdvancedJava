package edu.pdx.cs410J.laurente;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import edu.pdx.cs410J.InvokeMainTestCase;
import static junit.framework.Assert.assertEquals;

/**
 * Tests the functionality in the {@link Project1} main class.
 */
public class Project1Test extends InvokeMainTestCase {

    /**
     * Invokes the main method of {@link Project1} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
      return invokeMain( Project1.class, args );
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
    MainMethodResult result = invokeMain("Hawaiian Airlines", "234", "PDX", "3/02/2014 4:53", "HNL", "3/02/2014 21:53");
    assertEquals("03/02/2014 04:53", result.getOut());
  }

  @Ignore
  @Test
  public void testPrintOption() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDX", "3/02/2014 4:53", "HNL", "3/02/2014 21:53");
    System.out.println(result.getOut());
    //System.out.println(result.getExitCode());
  }

  @Test
  public void testInvalidFlightNumberAndPrintError() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "twothreefour", "PDX", "3/02/2014 4:53", "HNL", "3/02/2014 21:53");
    assertTrue(result.getErr().contains("Invalid argument: flight number must be a valid integer"));
  }

  @Test
  public void printErrWhenPrintArgWithNotEnoughRequiredArgs() {
    MainMethodResult result = invokeMain("-print", "twothreefour", "PDX", "3/02/2014 4:53", "HNL", "3/02/2014 21:53");
    assertTrue(result.getErr().contains("Insufficient number of arguments: Not enough information about the flight was given"));
  }

  @Test
  public void testInvalidSourceAirportCode() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDXz", "3/02/2014 4:53", "HNL", "3/02/2014 21:53");
    assertTrue(result.getErr().contains("Error: the source airport code \"PDXZ\" is not a valid code"));
  }

  @Test
  public void testInvalidDestAirportCode() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDX", "3/02/2014 4:53", "HNLz", "3/02/2014 21:53");
    assertTrue(result.getErr().contains("Error: the destination airport code \"HNLZ\" is not a valid code"));
  }

  @Test
  public void testPrintOptionWithValidArgs() {
    MainMethodResult result = invokeMain("-print", "Hawaiian Airlines", "234", "PDX", "3/2/2014 4:53", "HNL", "3/2/2014 21:53");
    assertTrue(result.getOut().contains("Hawaiian Airlines with 1 flight: Flight 234 departs PDX at 03/02/2014 04:53 arrives HNL at 03/02/2014 21:53"));
  }

  @Test
  public void testReadmeOption() {
    MainMethodResult result = invokeMain("-print", "-README","Hawaiian Airlines", "234", "PDX", "3/2/2014 4:53", "HNL", "3/2/2014 21:53");
    assertTrue(result.getOut().contains("This program prompts the user for an arline flight information"));

  }
}