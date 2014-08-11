package edu.pdx.cs410J.laurente.client;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;
import static com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.*;

import java.math.BigDecimal;
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
  public static final String ERROR_INVALID_DATE = "Invalid input: please enter a valid date in the \"mm/dd/yyyy\" format";
  public static final String ERROR_REMOTE_METHOD_FAILURE = "Error while invoking the remote method: ";
  private RootPanel rootPanel = RootPanel.get();
  private HeaderPanel headerPanel;
  private VerticalPanel addAFlightPanel;
  private TextBox airlineNameTextBox;
  private TextBox flightNumberTextBox;
  private ListBox airportSrcListBox;
  private Widget departureDatePanel;
  private ListBox airportDestListBox;
  private Widget arrivalDatePanel;
  private Airline theAirline = null;
  private String airlineName;
  private int flightNumber;
  private String srcAirportCode;
  private Date departureDateAndTime;
  private String destAirportCode;
  private Date arrivalDateAndTime;
  private Button addFlightButton;
  private Label tableAirlineName;
  private CellTable<Flight> flightsTable;
  private final Map<String, String> codesToNamesMapping = AirportNames.getNamesMap();
  private final List<String> airportCodesList = Arrays.asList(codesToNamesMapping.keySet().toArray(new String[0]));
  public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
  public static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm a");
  private AirlineServiceAsync serviceAsync;
  private List<Flight> listOfFlights;
  private ScrollPanel flightsTablePanel;

  public void onModuleLoad() {
    this.serviceAsync = GWT.create(AirlineService.class);
    //Sync airline object from server with the client
    this.serviceAsync.syncAirline(new AsyncCallback<AbstractAirline>() {
      @Override
      public void onFailure(Throwable caught) {
        Window.alert(ERROR_REMOTE_METHOD_FAILURE + caught.getMessage());
      }
      @Override
      public void onSuccess(AbstractAirline result) {
        theAirline = (Airline)result;
        if (theAirline != null) {
          airlineName = theAirline.getName();
        }
        updateFlightsTable();
      }
    });
    loadLayout();
  }

  private void loadLayout() {
    DockPanel p = new DockPanel();
    //p.add(new Label("header"), DockPanel.NORTH);
    //p.add(new Label("footer"), DockPanel.SOUTH);

    DecoratorPanel decoratedFormPanel = new DecoratorPanel();
    VerticalPanel middlePanel = new VerticalPanel();
    this.tableAirlineName = new Label();

    this.addAFlightPanel = createAddFlightForm();
    decoratedFormPanel.add(this.addAFlightPanel);
    setHandlers();
    loadTable();
    middlePanel.add(this.tableAirlineName);
    middlePanel.add(this.flightsTablePanel);
    middlePanel.setSpacing(10);
    p.add(decoratedFormPanel, DockPanel.WEST);
    p.add(middlePanel, DockPanel.WEST);
    p.setSpacing(10);
    rootPanel.add(p);

  }

  private static class Contact {
    private final String address;
    private final Date birthday;
    private final String name;

    public Contact(String name, Date birthday, String address) {
      this.name = name;
      this.birthday = birthday;
      this.address = address;
    }
  }

  public void loadTable() {
    // Create a CellTable.
    this.flightsTable = new CellTable<Flight>();
    this.flightsTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
    // Add the flight number column
    TextColumn<Flight> flightNumColumn = new TextColumn<Flight>() {
      @Override
      public String getValue(Flight object) {
        return String.valueOf(object.getNumber());
      }
    };
    this.flightsTable.addColumn(flightNumColumn, "Flight Number");
    // Add source airport column
    TextColumn<Flight> srcAiportColumn = new TextColumn<Flight>() {
      @Override
      public String getValue(Flight object) {
        String src = object.getSource();
        return codesToNamesMapping.get(src) + " (" + src +")";
      }
    };
    this.flightsTable.addColumn(srcAiportColumn, "Departing from");
    // Add departure column
    TextColumn<Flight> departureColumn = new TextColumn<Flight>() {
      @Override
      public String getValue(Flight object) {
        return object.getDepartureString();
      }
    };
    this.flightsTable.addColumn(departureColumn, "Departure date/time");
    // Add destination airport column
    TextColumn<Flight> destAiportColumn = new TextColumn<Flight>() {
      @Override
      public String getValue(Flight object) {
        String dest = object.getDestination();
        return codesToNamesMapping.get(dest) + " (" + dest +")";
      }
    };
    this.flightsTable.addColumn(destAiportColumn, "Arriving at");
    // Add arrival column
    TextColumn<Flight> arrivalColumn = new TextColumn<Flight>() {
      @Override
      public String getValue(Flight object) {
        return object.getArrivalString();
      }
    };
    this.flightsTable.addColumn(arrivalColumn, "Arrival date/time");
    // Add duration column
    TextColumn<Flight> durationColumn = new TextColumn<Flight>() {
      @Override
      public String getValue(Flight object) {
        // formatting for two decimal places
        NumberFormat fmt = NumberFormat.getDecimalFormat().overrideFractionDigits(0, 2);
        return String.valueOf(fmt.format(getFlightDuration(object.getDeparture(), object.getArrival())));
      }
    };
    this.flightsTable.addColumn(durationColumn, "Duration (hrs)");
    // Add a selection model to handle user selection.
    final SingleSelectionModel<Flight> selectionModel = new SingleSelectionModel<Flight>();
    this.flightsTable.setSelectionModel(selectionModel);
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      public void onSelectionChange(SelectionChangeEvent event) {
        Flight selected = selectionModel.getSelectedObject();
        if (selected != null) {
          Window.alert("You selected: " + selected.toString());
        }
      }
    });
    this.flightsTablePanel = new ScrollPanel(this.flightsTable);
    this.flightsTablePanel.setHeight("500px");
  }

  private void updateFlightsTable() {
    if (this.theAirline != null && this.theAirline.getFlights().size() >= 1) {
      this.tableAirlineName.setText("Airline: " + this.airlineName);
      this.listOfFlights = new ArrayList<Flight>(this.theAirline.getFlights());
      this.flightsTable.setRowCount(listOfFlights.size(), true);
      this.flightsTable.setRowData(0, listOfFlights);
    }
    else {
      this.flightsTable.setRowCount(0, true);
      this.flightsTable.setRowData(0, new ArrayList<Flight>());
      this.tableAirlineName.setText("NO AIRLINE");
    }
  }


  private Flight getFlightFromForm() {
    //get airline name
    String nameTemp = this.airlineNameTextBox.getValue();
    if (nameTemp == null || nameTemp.equals("") || nameTemp.equals(HINT_AIRLINE_NAME)) {
      Window.alert(ERROR_AIRLINENAME_MISSING);
      return null;
    }
    else if (this.theAirline != null && !this.theAirline.getName().equals(nameTemp)) {
      Window.alert(ERROR_AIRLINENAME_NOT_MATCH + "\"" + nameTemp + "\"");
      return null;
    }
    else {
      this.airlineName = nameTemp;
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
    //get departure date and time
    try {
      this.departureDateAndTime = parseDateAndTime(this.departureDatePanel);
    } catch (ParserException e) {
      Window.alert(e.getMessage() + " for the departure");
      return null;
    }
    //get arrival date and time
    try {
      this.arrivalDateAndTime = parseDateAndTime(this.arrivalDatePanel);
    } catch (ParserException e) {
      Window.alert(e.getMessage() + " for the arrival");
      return null;
    }
    return new Flight(this.flightNumber, this.srcAirportCode, this.departureDateAndTime, this.destAirportCode, this.arrivalDateAndTime);
  }

  private VerticalPanel createAddFlightForm() {
    VerticalPanel panel = new VerticalPanel();
    this.departureDatePanel = createDateTimePicker("Departure date and time");
    this.arrivalDatePanel = createDateTimePicker("Arrival date and time");
    this.airlineNameTextBox = new TextBox();
    this.flightNumberTextBox = new TextBox();
    this.airportSrcListBox = new ListBox();
    this.airportDestListBox = new ListBox();
    this.airlineNameTextBox.setText(HINT_AIRLINE_NAME);
    this.flightNumberTextBox.setText(HINT_FLIGHT_NUMBER);
    this.addFlightButton = new Button("Add flight");
    panel.add(this.airlineNameTextBox);
    panel.add(this.flightNumberTextBox);
    panel.add(getAirportCodePanel("Departing from", this.airportSrcListBox));
    panel.add(this.departureDatePanel);
    panel.add(getAirportCodePanel("Arriving at", this.airportDestListBox));
    panel.add(this.arrivalDatePanel);
    panel.add(this.addFlightButton);
    panel.setSpacing(6);
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

  private Date parseDateAndTime(Widget mainPanel) throws ParserException{
    StringBuilder result = new StringBuilder();
    Widget panel = ((ComplexPanel) mainPanel).getWidget(1);
    int numWidgets = ((ComplexPanel) panel).getWidgetCount();
    for (int i = 0; i < numWidgets; ++i) {
      Widget widget = ((ComplexPanel) panel).getWidget(i);
      if (widget instanceof DateBox) {
        DateBox dateBox = ((DateBox) widget);
        Date parsedDate = dateBox.getValue();
        if (parsedDate == null) {
          throw new ParserException(ERROR_INVALID_DATE);
        }
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

  private void setHandlers() {
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
    this.addFlightButton.addClickHandler(new ClickHandler() {
      public void onClick( ClickEvent clickEvent )
      {
        Flight flight = getFlightFromForm();
        if (flight != null) {
          serviceAsync.addAFlight(airlineName, flight, new AsyncCallback<AbstractAirline>() {
            @Override
            public void onFailure(Throwable caught) {
              Window.alert(ERROR_REMOTE_METHOD_FAILURE + caught.getMessage());
            }
            @Override
            public void onSuccess(AbstractAirline result) {
              theAirline = (Airline) result;
              updateFlightsTable();
            }
          });
        }
      }
    });
  }

  /**
   * This method calculates the duration of the flight from a filght's departure to arrival date/time values
   * @param depart    The departure date/time of the flight
   * @param arrive    The arrival date/time of the flight
   * @return          The flight duration in long
   */
  private double getFlightDuration(Date depart, Date arrive) {
    return (((arrive.getTime() - depart.getTime()) * 1d) / (3600000d));
  }
}
