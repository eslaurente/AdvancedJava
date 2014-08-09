package edu.pdx.cs410J.laurente.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirportNames;

import java.util.*;

/**
 * A basic GWT class that makes sure that we can send an airline back from the server
 */
public class AirlineGwt implements EntryPoint {
  private static final String HINT_AIRLINE_NAME = "Airline name";
  private static final String HINT_FLIGHT_NUMBER = "Flight number";
  private static final String AM = "AM";
  private static final String PM = "PM";
  public static final String ERROR_AIRLINENAME_MISSING = "Invalid input: please enter a valid airline name";
  public static final String ERROR_AIRLINENAME_NOT_MATCH = "Invalid input: the following does not match the airline name on the server: ";
  private RootPanel rootPanel = RootPanel.get();
  private HeaderPanel headerPanel;
  private TextBox airlineNameTextBox;
  private TextBox flightNumberTextBox;
  private ListBox airportSrcListBox;
  private Widget departureDatePanel;
  private ListBox airportDestListBox;
  private Widget arrivalDatePanel;
  private AbstractAirline theAirline = null;
  private String airlineName;
  private int flightNumber;
  private String srcAirportCode;
  private Date departureDateAndTime;
  private String destAirportCode;
  private Date arrivalDateAndTime;
  private final Map<String, String> codesToNamesMapping = AirportNames.getNamesMap();
  private final List<String> airportCodesList = Arrays.asList(codesToNamesMapping.keySet().toArray(new String[0]));
  public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
  public static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm a");


  public void onModuleLoad() {

    createLayout();
  }

  private void createLayout() {
    DockPanel p = new DockPanel();
    p.add(new Label("header"), DockPanel.NORTH);
    p.add(new Label("footer"), DockPanel.SOUTH);


    Button button = new Button("Add Flight");
    button.addClickHandler(new ClickHandler() {
      public void onClick( ClickEvent clickEvent )
      {
/*        AirlineServiceAsync async = GWT.create( AirlineService.class );

        async.ping( new AsyncCallback<AbstractAirline>() {

          public void onFailure( Throwable ex )
          {
            Window.alert(ex.toString());
          }

          public void onSuccess( AbstractAirline airline )
          {
            if (airline == null) {.text.SimpleDateFormat;

            }
            StringBuilder sb = new StringBuilder( airline.toString() );
            Collection<AbstractFlight> flights = airline.getFlights();
            for ( AbstractFlight flight : flights ) {
              sb.append(flight);
              sb.append("\n");
            }
            Window.alert( sb.toString() );
          }
        });*/
        getFlightFromForm();
      }
    });
    rootPanel.add(createAddFlightForm());
    setAddFlightFormHandler();
    rootPanel.add(button);
  }

  private AbstractFlight getFlightFromForm() {
    //get airline name
    this.airlineName = this.airlineNameTextBox.getValue();
    if (this.airlineName == null || this.airlineName.equals("") || this.airlineName.equals(HINT_AIRLINE_NAME)) {
      Window.alert(ERROR_AIRLINENAME_MISSING);
      return null;
    }
    else if (this.theAirline != null && !this.theAirline.getName().equals(this.airlineName)) {
      Window.alert(ERROR_AIRLINENAME_NOT_MATCH + "\"" + this.airlineName + "\"");
      return null;
    }
    //get flightNumber
    try {
      this.flightNumber = Integer.parseInt(this.flightNumberTextBox.getValue());
    } catch (NumberFormatException e) {
      Window.alert("Invalid input: please enter an integer for the flight number");
      return null;
    }
    //get airport source code
    this.srcAirportCode = parseAirportCode(this.airportSrcListBox);
    //get airport destination code
    this.destAirportCode = parseAirportCode(this.airportDestListBox);
    //get departure date
    this.departureDateAndTime = parseDateAndTime(this.departureDatePanel);
    this.arrivalDateAndTime = parseDateAndTime(this.arrivalDatePanel);
    Flight aFlight = new Flight(this.flightNumber, this.srcAirportCode, this.departureDateAndTime, this.destAirportCode, this.arrivalDateAndTime);
    Window.alert(aFlight.toString());
    return aFlight;
  }

  private Widget createAddFlightForm() {
    VerticalPanel panel = new VerticalPanel();
    this.departureDatePanel = createDateTimePicker("Departure date and time");
    this.arrivalDatePanel = createDateTimePicker("Arrival date and time");
    this.airlineNameTextBox = new TextBox();
    this.flightNumberTextBox = new TextBox();
    this.airportSrcListBox = new ListBox();
    this.airportDestListBox = new ListBox();
    this.airlineNameTextBox.setText(HINT_AIRLINE_NAME);
    this.flightNumberTextBox.setText(HINT_FLIGHT_NUMBER);
    panel.add(this.airlineNameTextBox);
    panel.add(this.flightNumberTextBox);
    panel.add(getAirportCodePanel("Departing from", this.airportSrcListBox));
    panel.add(this.departureDatePanel);
    panel.add(getAirportCodePanel("Arriving at", this.airportDestListBox));
    panel.add(this.arrivalDatePanel);
    panel.setSpacing(6);
    //Window.alert("Time: " + parseDateAndTime(this.departureDatePanel));
    //Window.alert("Airport code: " + parseAirportCode(this.airportSrcListBox));
    return panel;
  }


  private VerticalPanel getAirportCodePanel(String title, ListBox listBoxSource) {
    VerticalPanel panel = new VerticalPanel();
    populateAirportCode(listBoxSource);
    listBoxSource.setWidth("235px");
    panel.add(new Label(title + ": "));
    panel.add(listBoxSource);
    return panel;
  }

  private void populateAirportCode(ListBox list) {
    for (String code : airportCodesList) {
      list.addItem(codesToNamesMapping.get(code) + " (" + code + ")");
    }
    list.setSelectedIndex(0);
  }

  private VerticalPanel createDateTimePicker(String title) {
    VerticalPanel panel = new VerticalPanel();
    HorizontalPanel datePanel = new HorizontalPanel();
    DateBox date = new DateBox();
    date.setValue(new Date());
    HorizontalPanel timePicker = getTimePicker();
    date.setFormat(new DateBox.DefaultFormat(DATE_FORMAT));
    date.setWidth("80px");
    datePanel.add(date);
    datePanel.add(timePicker);
    datePanel.setSpacing(2);
    panel.add(new Label("   " + title + ":"));
    panel.add(datePanel);
    return panel;
  }

  private HorizontalPanel getTimePicker() {
    HorizontalPanel panel = new HorizontalPanel();
    ListBox hour = new ListBox(false);
    ListBox minutes = new ListBox(false);
    ListBox amPm = new ListBox(false);
    Label colon = new Label(":");
    for (int i = 1; i <= 12; ++i) {
      if ((Math.floor(Math.log10(i)) + 1) < 2) {
        hour.addItem("0" + String.valueOf(i));
      }
      else {
        hour.addItem(String.valueOf(i));
      }
    }
    for (int i = 0; i <= 59; ++i) {
      if ((Math.floor(Math.log10(i)) + 1) < 2) {
        minutes.addItem("0" + String.valueOf(i));
      }
      else {
        minutes.addItem(String.valueOf(i));
      }
    }
    amPm.addItem(AM);
    amPm.addItem(PM);
    panel.add(hour);
    panel.add(colon);
    panel.add(minutes);
    panel.add(amPm);
    return panel;
  }

  private Date parseDateAndTime(Widget mainPanel) {
    StringBuilder result = new StringBuilder();
    Widget panel = ((ComplexPanel) mainPanel).getWidget(1);
    int numWidgets = ((ComplexPanel) panel).getWidgetCount();
    for (int i = 0; i < numWidgets; ++i) {
      Widget widget = ((ComplexPanel) panel).getWidget(i);
      if (widget instanceof DateBox) {
        DateBox dateBox = ((DateBox) widget);
        result.append(DATE_FORMAT.format(dateBox.getValue())).append(" ");
      }
      else if (widget instanceof HorizontalPanel) {
        HorizontalPanel timePanel = ((HorizontalPanel) widget);
        int numWidgets1 = timePanel.getWidgetCount();
        for (int j = 0; j < numWidgets1; ++j) {
          Widget widget1 = timePanel.getWidget(j);
          if (widget1 instanceof Label) {
            result.append(":");
          }
          else if (widget1 instanceof ListBox) {
            int selIndex = ((ListBox) widget1).getSelectedIndex();
            String selection = ((ListBox) widget1).getValue(selIndex);
            if (selection.equals(AM) || selection.equals(PM)) {
              result.append(" ").append(selection);
            } else {
              result.append(selection);
            }
          }
        }
      }
    }
    return DATE_TIME_FORMAT.parse(result.toString());
  }

  private String parseAirportCode(ListBox listbox) {
    String airportCode = "";
    int selectionIndex = listbox.getSelectedIndex();
    airportCode = airportCodesList.get(selectionIndex);
    return airportCode;
  }

  private void setAddFlightFormHandler() {
    this.airlineNameTextBox.addFocusHandler(new FocusHandler() {
      @Override
      public void onFocus(FocusEvent event) {
        if (airlineNameTextBox.getValue().equals(HINT_AIRLINE_NAME)) {
          airlineNameTextBox.setValue("");
        }
      }
    });
    this.flightNumberTextBox.addFocusHandler(new FocusHandler() {
      @Override
      public void onFocus(FocusEvent event) {
        if (flightNumberTextBox.getValue().equals(HINT_FLIGHT_NUMBER)) {
          flightNumberTextBox.setValue("");
        }
      }
    });
  }
}
