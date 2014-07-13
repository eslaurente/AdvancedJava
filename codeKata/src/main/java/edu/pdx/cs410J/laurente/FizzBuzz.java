package edu.pdx.cs410J.laurente;

/**
 * Created by emerald on 7/9/14.
 */
public class FizzBuzz {
  public static String parseNumber(int number) {
    if (number % 3 == 0 && number % 5 == 0) {
      return "FizzBuzz";
    }
    else if (number % 3 == 0) {
      return "Fizz";
    } else if (number % 5 == 0) {
      return "Buzz";
    }
    return String.valueOf(number);
  }

  public static void main(String[] args) {
    for(int i = 1; i <= 100; ++i) {
      System.out.println((FizzBuzz.parseNumber(i)));
    }
  }
}
