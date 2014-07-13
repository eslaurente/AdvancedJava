package edu.pdx.cs410J.laurente;

import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import edu.pdx.cs410J.InvokeMainTestCase;
import static junit.framework.Assert.assertEquals;

/**
 * Created by emerald on 7/9/14.
 */
public class FizzBuzzTest {

  @Test
  public void countOneToThree() {
    assertEquals(FizzBuzz.parseNumber(1), "1");
    assertEquals(FizzBuzz.parseNumber(2), "2");
    assertEquals(FizzBuzz.parseNumber(3), "Fizz");
  }

  @Test
  public void countFourToSix() {
    assertEquals(FizzBuzz.parseNumber(4), "4");
    assertEquals(FizzBuzz.parseNumber(5), "Buzz");
    assertEquals(FizzBuzz.parseNumber(6), "Fizz");
  }

  @Test
  public void testDivisibleByThreeAndFive() {
    assertEquals(FizzBuzz.parseNumber(15), "FizzBuzz");
  }
}
