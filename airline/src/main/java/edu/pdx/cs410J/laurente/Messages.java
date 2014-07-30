package edu.pdx.cs410J.laurente;

/**
* Class for formatting messages on the server side.  This is mainly to enable
* test methods that validate that the server returned expected strings.
*/
public class Messages
{
  public static String getMappingCount( int count )
  {
    return String.format( "Server contains %d key/value pairs", count );
  }

  public static String formatKeyValuePair( String key, String value )
  {
    return String.format("  %s -> %s", key, value);
  }

  public static String missingRequiredParameter( String key )
  {
    return String.format("The required parameter \"%s\" is missing", key);
  }

  public static String mappedKeyValue( String key, String value )
  {
    return String.format( "Mapped %s to %s", key, value );
  }
}
