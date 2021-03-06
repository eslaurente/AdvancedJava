/**
 *
 */
package edu.pdx.cs410J.laurente.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;
import static com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.*;

import java.util.*;

/**
 * This class implements a GWT EntryPoint which loads the user interface and instantiates the EventHandlers and data
 * synchronization for the Airline Flight Management Program.
 */
public class AirlineGwt implements EntryPoint {
  private static final String HINT_AIRLINE_NAME = "Airline name";
  private static final String HINT_FLIGHT_NUMBER = "Flight number";
  private static final String AM = "AM";
  private static final String PM = "PM";
  private static final String ERROR_AIRLINENAME_MISSING = "INVALID INPUT: please enter a valid airline name";
  private static final String ERROR_AIRLINENAME_NOT_MATCH = "INVALID INPUT: the following does not match the airline name on the server: ";
  private static final String ERROR_INVALID_DATE = "INVALID INPUT: please enter a valid date in the \"mm/dd/yyyy\" format";
  private static final String ERROR_REMOTE_METHOD_FAILURE = "ERROR while invoking the remote method: ";
  private static final String FLIGHT_SEARCH_PREFIX = "flight:";
  private static final String SRC_SEARCH_PREFIX = "src:";
  private static final String DEST_SEARCH_PREFIX = "dest:";
  private RootPanel rootPanel = RootPanel.get();
  private HeaderPanel headerPanel;
  private VerticalPanel addAFlightPanel;
  private TextBox airlineNameTextBox;
  private TextBox flightNumberTextBox;
  private ListBox airportSrcListBox;
  private Widget departureDatePanel;
  private ListBox airportDestListBox;
  private Widget arrivalDatePanel;
  private Airline theAirline; //the working Airline object for the client
  private String airlineName;
  private Button addFlightButton;
  private Label tableAirlineName;
  private CellTable<Flight> flightsTable;
  private final Map<String, String> codesToNamesMapping = AirportNames.getNamesMap();
  private final List<String> airportCodesList = Arrays.asList(codesToNamesMapping.keySet().toArray(new String[0]));
  private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
  private static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm a");
  private AirlineServiceAsync serviceAsync;
  private List<Flight> listOfFlights;
  private ScrollPanel flightsTablePanel;
  private SuggestBox searchBox;
  private Button searchButton;
  private Button deleteFlightButton;
  private Flight flightToDelete;
  private List<String> searchQuery;
  private static final List<String> SEARCH_COMMANDS_LIST = new ArrayList<String>(Arrays.asList("flight:#", "src:ABC", "dest:XYZ", "src:ABC dest:XYZ"));
  private static final String SEARCH_USAGE = buildSearchFlightUsage();
  private static final String README = buildReadMeText();

  /**
   * This is an inner helper class that allows this program to display its messages as an animated pop-up DialogBox
   */
  private static class PopupDialogBox extends DialogBox {
    public boolean promptBooleanValue = false;

    /**
     * This is a constructor for the pup-up dialog box which displays the parameter text and can be displayed as a
     * yes-or-no prompt or a normal message box
     * @param text      The message to be displayed
     * @param isPrompt  The boolean value of whether to create a yes-or-no prompt or a normal message box
     */
    public PopupDialogBox(String text, boolean isPrompt) {
      setAnimationEnabled(true);
      setGlassEnabled(true);
      setAutoHideEnabled(true);
      Button ok = null;
      Button yes = null;
      Button no = null;
      if (isPrompt == false) {
        ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            PopupDialogBox.this.hide();
          }
        });
      }
      else {
        setAutoHideEnabled(false);
        yes = new Button("Yes");
        no = new Button("No");
        yes.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            PopupDialogBox.this.promptBooleanValue = true;
            PopupDialogBox.this.hide();
          }
        });
        no.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            PopupDialogBox.this.promptBooleanValue = false;
            PopupDialogBox.this.hide();
          }
        });
      }
      VerticalPanel panel = new VerticalPanel();
      panel.setSpacing(10);
      HTML html = new HTML(new SafeHtmlBuilder().appendEscapedLines(text).toSafeHtml());
      panel.add(html);
      panel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
      panel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
      if (isPrompt == false) {
        panel.add(ok);
      }
      else {
        HorizontalPanel yesNoPanel = new HorizontalPanel();
        yesNoPanel.add(yes);
        yesNoPanel.add(no);
        yesNoPanel.setSpacing(10);
        panel.add(yesNoPanel);
      }
      setWidget(panel);
      center();
    }
  }

  /**
   * This method is the entry method that calls reloadTableWithAllFlights() and loadLayout() to initiate the interface
   */
  public void onModuleLoad() {
    Window.setTitle("Airline Flight Management Program");
    this.theAirline = null;
    this.serviceAsync = GWT.create(AirlineService.class);
    //Sync airline object from server with the client
    reloadTableWithAllFlights();
    loadLayout();
  }

  /**
   * This method calls the async method syncAirline() to sync the working airline object, theAirline, and update the
   * table of flights Widget to list all of its flights.
   */
  private void reloadTableWithAllFlights() {
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
        else {
          airlineName = "";
        }
        updateFlightsTable(false, null);
      }
    });
  }

  /**
   * This method instantiates and configures the panel for the main user interface
   */
  private void loadLayout() {
    DockPanel p = new DockPanel();
    DecoratorPanel decoratedFormPanel = new DecoratorPanel();
    VerticalPanel middlePanel = new VerticalPanel();
    this.tableAirlineName = new Label();
    this.addAFlightPanel = createAddFlightForm();
    this.deleteFlightButton = new Button();
    this.deleteFlightButton.setText("Delete Flight");
    this.deleteFlightButton.setEnabled(false);
    decoratedFormPanel.add(this.addAFlightPanel);
    setHandlers();
    loadTable();
    middlePanel.setWidth("100%");
    middlePanel.setHeight("100%");
    middlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    middlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    middlePanel.add(configureSearchBox());
    middlePanel.add(this.tableAirlineName);
    middlePanel.add(this.flightsTablePanel);
    middlePanel.add(this.deleteFlightButton);
    middlePanel.setSpacing(10);
    p.add(decoratedFormPanel, DockPanel.WEST);
    p.add(middlePanel, DockPanel.WEST);
    p.add(menuBarPanel(), DockPanel.CENTER);
    p.setSpacing(10);
    this.tableAirlineName.getElement().getStyle().setFontSize(16, Style.Unit.PX);
    this.tableAirlineName.getElement().getStyle().setFontStyle(Style.FontStyle.ITALIC);
    this.rootPanel.add(p);

  }

  /**
   * This button instantiates and configures the Help menu bar on the top right corner. The README and About options are
   * also configured here, and are added to the panel that houses them.
   * @return     The menu bar panel with the Help menu instantiated
   */
  private HorizontalPanel menuBarPanel() {
    HorizontalPanel panel = new HorizontalPanel();
    MenuBar menuBar = new MenuBar();
    MenuBar help = new MenuBar(true);
    menuBar.addItem("Help", help);
    help.addItem("README", new Command() {
      @Override
      public void execute() {
        new PopupDialogBox(README, false).show();
      }
    });
    help.addItem("About", new Command() {
      @Override
      public void execute() {
        StringBuilder about = new StringBuilder();
        about.append("This program is for the final project (Project 5) for David Whitlock's\nCS410J Advanced Java Programming course at PSU.\n");
        about.append("For educational purposes only");
        about.append("\n\n@Author: Emerald Laurente");
        new PopupDialogBox(about.toString(), false).show();
      }
    });
    panel.add(menuBar);
    return panel;
  }

  /**
   * This method configures the panel that houses the searchBox SuggestionBox and the searchButton Button Widget objects
   * @return    The instatiated search panel
   */
  private HorizontalPanel configureSearchBox() {
    HorizontalPanel panel = new HorizontalPanel();
    this.searchButton = new Button();
    this.searchButton.setText("Search");
    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    oracle.setDefaultSuggestionsFromText(SEARCH_COMMANDS_LIST);
    this.searchBox = new SuggestBox(oracle);
    this.searchBox.getTextBox().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        searchBox.showSuggestionList();
      }
    });
    setSearchButtonHandler();
    panel.add(this.searchBox);
    panel.add(this.searchButton);
    panel.setSpacing(10);
    return panel;
  }

  /**
   * This method configures the behavior and logic of the search button, which extracts the actual search query arguments
   * from searchBox SuggestionBox field. searchButton click event hadnling, parsing, and error handling is done here.
   */
  private void setSearchButtonHandler() {
    this.searchButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String actual = searchBox.getText();
        if (actual.equals("") || theAirline == null) {
          reloadTableWithAllFlights();
          return;
        }
        searchQuery = new ArrayList<String>(Arrays.asList(searchBox.getText().split("\\s")));
        if (searchQuery.size() == 2) {
          String str1 = searchQuery.get(0);
          String str2 = searchQuery.get(1);
          if ((str1.startsWith(SRC_SEARCH_PREFIX) || str1.startsWith(DEST_SEARCH_PREFIX)) && (str2.startsWith(SRC_SEARCH_PREFIX) || str2.startsWith(DEST_SEARCH_PREFIX)) && (!str1.equals(str2))) {
            try {
              if (str1.startsWith(SRC_SEARCH_PREFIX)) {
                searchFlightBySrcAndDest(str1.replaceAll(SRC_SEARCH_PREFIX, ""), str2.replaceAll(DEST_SEARCH_PREFIX, ""));
              }
              else {
                searchFlightBySrcAndDest(str2.replaceAll(SRC_SEARCH_PREFIX, ""), str1.replaceAll(DEST_SEARCH_PREFIX, ""));
              }
            } catch (ParserException e) {
              new PopupDialogBox(e.getMessage() + SEARCH_USAGE, false).show();
            }
          }
          else {
            new PopupDialogBox("INVALID INPUT: " + str1 + " " + str2 + " is not a valid search command\n\n" + SEARCH_USAGE, false).show();
          }
        }
        else if (searchQuery.size() == 1) {
          String str = searchQuery.get(0);
          if (str.startsWith(FLIGHT_SEARCH_PREFIX)) {
            try {
              searchByFlightNumber(str.replaceFirst(FLIGHT_SEARCH_PREFIX, ""));
            } catch (ParserException e) {
              new PopupDialogBox(e.getMessage() + SEARCH_USAGE, false).show();
            }
          }
          else if (str.startsWith(SRC_SEARCH_PREFIX)) {
            try {
              searchFlightBySrc(str.replaceFirst(SRC_SEARCH_PREFIX, ""));
            } catch (ParserException e) {
              new PopupDialogBox(e.getMessage() + SEARCH_USAGE, false).show();
            }
          }
          else if (str.startsWith(DEST_SEARCH_PREFIX)) {
            try {
              searchFlightByDest(str.replaceFirst(DEST_SEARCH_PREFIX, ""));
            } catch (ParserException e) {
              new PopupDialogBox(e.getMessage() + SEARCH_USAGE, false).show();
            }
          }
          else {
            new PopupDialogBox("INVALID INPUT: " + str + " is not a valid search command\n\n" + SEARCH_USAGE, false).show();
          }
        }
        else {
          new PopupDialogBox("INVALID INPUT:\n\n" + SEARCH_USAGE, false).show();
        }
      }
    });
  }

  /**
   * This method updates the list of flights table that matches flights from the current working airline with both the
   * airport source AND destination code that is the parameter. The async method syncAirline() is first called to
   * ensure the most recent state of the airline is being used.
   * @param src               The airport source code to be parsed and searched with
   * @param dest              The airport destination code to be parsed and searched with
   * @throws ParserException  Thrown if the either one or both of src and dest is not in the list of known airport codes
   */
  private void searchFlightBySrcAndDest(String src, String dest) throws ParserException {
    final String srcCode = src.toUpperCase();
    final String destCode = dest.toUpperCase();
    boolean srcValid = isValidAirportCode(srcCode);
    boolean destValid = isValidAirportCode(destCode);
    if ((srcValid == destValid) && (srcValid == false)) {
      throw new ParserException("INVALID INPUT: both the src and the dest arguments are not valid airport codes\n\n");
    }
    if (srcValid == false) {
      throw new ParserException("INVALID INPUT: the src argument is not a valid airport code\n\n");
    }
    if (destValid == false) {
      throw new ParserException("INVALID INPUT: the dest argument is not a valid airport code\n\n");
    }
    this.serviceAsync.syncAirline(new AsyncCallback<AbstractAirline>() {
      @Override
      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage() + "\n...working with client data...");
        List<Flight> resultingList = new ArrayList<Flight>();
        for (Object object : theAirline.getFlights()) {
          Flight aFlight = ((Flight) object);
          if (aFlight.getSource().equals(srcCode) && aFlight.getDestination().equals(destCode)) {
            resultingList.add(aFlight);
          }
        }
        updateFlightsTable(false, null);
      }
      @Override
      public void onSuccess(AbstractAirline result) {
        theAirline = ((Airline) result);
        List<Flight> resultingList = new ArrayList<Flight>();
        for (Object object : theAirline.getFlights()) {
          Flight aFlight = ((Flight) object);
          if (aFlight.getSource().equals(srcCode) && aFlight.getDestination().equals(destCode)) {
            resultingList.add(aFlight);
          }
        }
        updateFlightsTable(true, resultingList);
      }
    });
  }

  /**
   * This method updates the list of flights table that matches flights from the current working airline with the
   * airport destination code that is the parameter. The async method syncAirline() is first called to ensure the most recent
   * state of the airline is being used.
   * @param airportCode           The airport code to be parsed and searched with
   * @throws ParserException      Thrown if airportCode is not in the list of known airport codes
   */
  private void searchFlightByDest(String airportCode) throws ParserException {
    final String code = airportCode.toUpperCase();
    if (isValidAirportCode(code) == true) {
      this.serviceAsync.syncAirline(new AsyncCallback<AbstractAirline>() {
        @Override
        public void onFailure(Throwable caught) {
          Window.alert(caught.getMessage() + "\n...working with client data...");
          List<Flight> resultingList = new ArrayList<Flight>();
          for (Object object : theAirline.getFlights()) {
            Flight aFlight = ((Flight) object);
            if (aFlight.getDestination().equals(code)) {
              resultingList.add(aFlight);
            }
          }
          updateFlightsTable(false, null);
        }
        @Override
        public void onSuccess(AbstractAirline result) {
          theAirline = ((Airline) result);
          List<Flight> resultingList = new ArrayList<Flight>();
          for (Object object : theAirline.getFlights()) {
            Flight aFlight = ((Flight) object);
            if (aFlight.getDestination().equals(code)) {
              resultingList.add(aFlight);
            }
          }
          updateFlightsTable(true, resultingList);
        }
      });
    }
    else {
      throw new ParserException("INVALID INPUT: the dest argument is not a valid airport code\n\n");
    }
  }

  /**
   * This method updates the list of flights table that matches flights from the current working airline with the
   * airport source code that is the parameter. The async method syncAirline() is first called to ensure the most recent
   * state of the airline is being used.
   * @param airportCode       The airport code to be parsed and searched with
   * @throws ParserException  Thrown if airportCode is not in the known-list of airports
   */
  private void searchFlightBySrc(String airportCode) throws ParserException {
    final String code = airportCode.toUpperCase();
    if (isValidAirportCode(code) == true) {
      this.serviceAsync.syncAirline(new AsyncCallback<AbstractAirline>() {
        @Override
        public void onFailure(Throwable caught) {
          Window.alert(caught.getMessage() + "\n...working with client data...");
          List<Flight> resultingList = new ArrayList<Flight>();
          for (Object object : theAirline.getFlights()) {
            Flight aFlight = ((Flight) object);
            if (aFlight.getSource().equals(code)) {
              resultingList.add(aFlight);
            }
          }
          updateFlightsTable(false, null);
        }
        @Override
        public void onSuccess(AbstractAirline result) {
          theAirline = ((Airline) result);
          List<Flight> resultingList = new ArrayList<Flight>();
          for (Object object : theAirline.getFlights()) {
            Flight aFlight = ((Flight) object);
            if (aFlight.getSource().equals(code)) {
              resultingList.add(aFlight);
            }
          }
          updateFlightsTable(true, resultingList);
        }
      });
    }
    else {
      throw new ParserException("INVALID INPUT: the src argument is not a valid airport code\n\n");
    }
  }

  private boolean isValidAirportCode(String s) {
    return codesToNamesMapping.get(s) != null ? true : false;
  }

  /**
   * This method searches the list of flights of the working airline by flight number. The async method syncAirline() is called
   * first to ensure that the most current state of the Airline object is in sync with the working Airline object, theAirline. In
   * addition, this method first parses the String representation of the flight before anything.
   * @param flightNumberStr     The String representation of a Flight's object's flight number
   * @throws ParserException    Thrown if flightNumberStr is not a valid int/Integer object
   */
  private void searchByFlightNumber(String flightNumberStr) throws ParserException {
    final int flightNum;
    try {
      flightNum = Integer.parseInt(flightNumberStr);
    } catch (NumberFormatException e) {
      throw new ParserException("INVALID INPUT: valid integer for flight number is required\n\n");
    }
    //sync the airline from server
    this.serviceAsync.syncAirline(new AsyncCallback<AbstractAirline>() {
      @Override
      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage() + "\n...working with client data...");
        List<Flight> resultingList = new ArrayList<Flight>();
        for (Object object : theAirline.getFlights()) {
          Flight aFlight = ((Flight) object);
          if (aFlight.getNumber() == flightNum) {
            resultingList.add(aFlight);
          }
        }
        updateFlightsTable(false, null);
      }
      @Override
      public void onSuccess(AbstractAirline result) {
        theAirline = ((Airline) result);
        List<Flight> resultingList = new ArrayList<Flight>();
        for (Object object : theAirline.getFlights()) {
          Flight aFlight = ((Flight) object);
          if (aFlight.getNumber() == flightNum) {
            resultingList.add(aFlight);
          }
        }
        updateFlightsTable(true, resultingList);
      }
    });
  }

  /**
   * This method will remove a Flight object by calling the async method deleteFlight() as well as displaying a popup
   * confirmation before deletion. After a flight deletion, the flights table will be updated.
   * @param flight    The Flight object to be deleted
   */
  private void deleteAFlight(final Flight flight) {
    final DecoratedPopupPanel popup = new DecoratedPopupPanel(true);
    popup.center();
    serviceAsync.deleteFlight(flight, new AsyncCallback<Boolean>() {
      @Override
      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage() + "\n...working with client data...");
        if (theAirline.getFlights().remove(flight) == false) {
          popup.add(new Label("ERROR: Could not remove that flight!"));
        }
        else {
          popup.add(new Label("Flight successfully deleted!"));
        }
        reloadTableWithAllFlights();
      }
      @Override
      public void onSuccess(Boolean result) {
        if(result == true) {
          popup.add(new Label("Flight successfully deleted!"));
        }
        else {
          popup.add(new Label("ERROR: Could not remove that flight!"));
        }
        popup.show();
        searchBox.setText("");
        reloadTableWithAllFlights();
      }
    });
  }

  /**
   * This method will instantiate and configure the cell table, using CellTable, which displays information about a list of flights,
   * per column: the flight number, departure airport source, departure date and time, arrival airport destination, arrival date and time,
   * the flight's duration in hours. A SelectionModel object is also created to get the Flight object selected, used for the deleting feature.
   */
  private void loadTable() {
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
        deleteFlightButton.setEnabled(true);
        Flight selected = selectionModel.getSelectedObject();
        if (selected != null) {
          flightToDelete = selected;
          //new PopupDialogBox(airlineName + ": " + selected.toString()).show();
        }
      }
    });
    this.flightsTablePanel = new ScrollPanel(this.flightsTable);
    this.flightsTablePanel.setHeight("350px");
  }

  /**
   * This method updates the table of flights according to the either the current working airline (if it exists) or form
   * an alternative list of flights from a resulting search inquiry
   * @param altDataProvider     The boolean value of whether or not this update will use the working airline object or
   *                            the alternativeList list of Flight objects
   * @param alternativeList     The alternative list of Flight objects to update the list with
   */
  private void updateFlightsTable(boolean altDataProvider, List<Flight> alternativeList) {
    if (altDataProvider == false && this.theAirline != null && this.theAirline.getFlights().size() >= 1) {
      this.tableAirlineName.setText("AIRLINE: " + this.airlineName);
      this.listOfFlights = new ArrayList<Flight>(this.theAirline.getFlights());
      this.flightsTable.setRowCount(listOfFlights.size(), true);
      this.flightsTable.setRowData(0, listOfFlights);
      this.searchBox.setText("");
      if (this.listOfFlights.size() == 1) {
        this.flightToDelete = this.listOfFlights.get(0);
        this.deleteFlightButton.setEnabled(true);
      }
    }
    else if (altDataProvider == true) {
      this.tableAirlineName.setText("AIRLINE: " + this.airlineName);
      this.flightsTable.setRowCount(alternativeList.size(), true);
      this.flightsTable.setRowData(0, alternativeList);
      if (alternativeList.size() == 1) {
        this.flightToDelete = alternativeList.get(0);
        this.deleteFlightButton.setEnabled(true);
      }
    }
    else {
      this.flightsTable.setRowCount(0, true);
      this.flightsTable.setRowData(0, new ArrayList<Flight>());
      this.tableAirlineName.setText("NO AIRLINE");
      this.deleteFlightButton.setEnabled(false);
    }
  }

  /**
   * This method starts the extraction of the add-a-flight form's Widgets' values. The parsing logic is implemented
   * in this method, as well.
   * @return    The Flight object that was created from a successful parsing of data for a flight. Null value is returned
   *            if the parsing process encounters a problem
   */
  private Flight getFlightFromForm() {
    //get airline name
    String nameTemp = this.airlineNameTextBox.getValue();
    int flightNumber;
    String srcAirportCode, destAirportCode;
    Date arrivalDateAndTime, departureDateAndTime;
    if (nameTemp == null || nameTemp.equals("") || nameTemp.equals(HINT_AIRLINE_NAME)) {
      new PopupDialogBox(ERROR_AIRLINENAME_MISSING, false).show();
      return null;
    }
    else if (this.theAirline != null && !this.theAirline.getName().equals(nameTemp)) {
      new PopupDialogBox(ERROR_AIRLINENAME_NOT_MATCH + "\"" + nameTemp + "\"", false).show();
      return null;
    }
    else {
      this.airlineName = nameTemp;
    }
    //get flightNumber
    try {
      flightNumber = Integer.parseInt(this.flightNumberTextBox.getValue());
    } catch (NumberFormatException e) {
      new PopupDialogBox("INVALID INPUT: please enter an integer for the flight number", false).show();
      return null;
    }
    //get airport source code
    srcAirportCode = extractAriportCode(this.airportSrcListBox);
    //get airport destination code
    destAirportCode = extractAriportCode(this.airportDestListBox);
    //get departure date and time
    try {
      departureDateAndTime = parseDateAndTime(this.departureDatePanel);
    } catch (ParserException e) {
      new PopupDialogBox(e.getMessage() + " for the departure", false).show();
      return null;
    }
    //get arrival date and time
    try {
      arrivalDateAndTime = parseDateAndTime(this.arrivalDatePanel);
    } catch (ParserException e) {
      new PopupDialogBox(e.getMessage() + " for the arrival", false).show();
      return null;
    }
    //invalid input if departure date/time is after arrival date/time
    if (getFlightDuration(departureDateAndTime, arrivalDateAndTime) < 0.0) {
      new PopupDialogBox("INVALID INPUT: could not add flight because arrival date/time precedes departure date/time", false).show();
      return null;
    }
    return new Flight(flightNumber, srcAirportCode, departureDateAndTime, destAirportCode, arrivalDateAndTime);
  }

  /**
   * This method sets up the add-a-flight form using a VerticalPanel panel, that has Widgets for the airline's name, flight's
   * number, airoport source and destination listbox chooser, and departure and arrival date and time pickers. Finally a submit
   * button to signal parsing of the widgets' values.
   * @return    The panel that has the add-a-flight form
   */
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

  /**
   * This method sets up a panel with appropriate sizeing and labeling for an airport code listbox chooser
   * @param title           The panel label for this listbox chooser
   * @param listBoxSource   The ListBox object to be added to this panel
   * @return                The panel created with the label followed by the listbox
   */
  private VerticalPanel getAirportCodePanel(String title, ListBox listBoxSource) {
    VerticalPanel panel = new VerticalPanel();
    populateAirportCode(listBoxSource);
    listBoxSource.setWidth("235px");
    panel.add(new Label(title + ": "));
    panel.add(listBoxSource);
    return panel;
  }

  /**
   * This method iterates through the list of airport codes populates a ListBox object's values with each item
   * @param list     The ListBox list with populated values
   */
  private void populateAirportCode(ListBox list) {
    for (String code : airportCodesList) {
      list.addItem(codesToNamesMapping.get(code) + " (" + code + ")");
    }
    list.setSelectedIndex(0);
  }

  /**
   * This method creates a panel that houses both the date picker and custom time picker
   * @param title    The label that precedes the date and time picker
   * @return         The panel that houses both the date and time picker
   */
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

  /**
   * This method creates a custom time picker that allows a user to choose the hour, minute, and am|pm marker for a time
   * @return      The panel that contains a series of ListBox objects representing a time picker
   */
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

  /**
   * This method parses the date and time from a DateBox object and a series of ListBox objects that represent the time
   * @param mainPanel         The panel that contains the DateBox object and ListBox ojbects
   * @return                  The Date object representation from parsing
   * @throws ParserException  Thrown if the DateBox component parses and returns a  null values
   */
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

  /**
   * This method extracts the selected text from a ListBox object
   * @param listbox     The ListBox object to be extracted
   * @return            The airport code equivalent of the selected value
   */
  private String extractAriportCode(ListBox listbox) {
    String airportCode = "";
    int selectionIndex = listbox.getSelectedIndex();
    airportCode = airportCodesList.get(selectionIndex);
    return airportCode;
  }

  /**
   * This method sets event handlers for various Widget components of the program
   */
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
      public void onClick( final ClickEvent clickEvent )
      {
        final DecoratedPopupPanel popup = new DecoratedPopupPanel(true);
        Widget source = (Widget) clickEvent.getSource();
        int left = source.getAbsoluteLeft() + 10;
        int top = source.getAbsoluteTop() + 10;
        popup.setPopupPosition(left, top);
        //parse add-a-flight form
        Flight flight = getFlightFromForm();
        if (flight != null) {
          serviceAsync.addAFlight(airlineName, flight, new AsyncCallback<AbstractAirline>() {
            @Override
            public void onFailure(Throwable caught) {
              Window.alert(ERROR_REMOTE_METHOD_FAILURE + caught.getMessage());
            }
            @Override
            public void onSuccess(AbstractAirline result) {
              int priorSize = 0, currentSize = 0;
              if (theAirline != null) {
                priorSize = theAirline.getFlights().size();
              }
              theAirline = (Airline) result;
              if (theAirline != null) {
                currentSize = theAirline.getFlights().size();
              }
              // Show the popup
              if (currentSize > priorSize) {
                popup.add(new Label("Flight successfully added!"));
              }
              else {
                popup.add(new Label("That flight already exists!"));
              }
              popup.show();
              updateFlightsTable(false, null);
            }
          });
        }
        //else flight is null, and errors should have been displayed. Nothing has changed
      }
    });
    this.deleteFlightButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final PopupDialogBox yesNo = new PopupDialogBox("Are you sure you want to delete the selected flight?\nThis cannot be undone.", true);
        yesNo.show();
        yesNo.addCloseHandler(new CloseHandler<PopupPanel>() {
          @Override
          public void onClose(CloseEvent<PopupPanel> event) {
            if(yesNo.promptBooleanValue == true) {
              deleteAFlight(flightToDelete);
            }
          }
        });
      }
    });
  }

  /**
   * This method builds the README which details the program's functionality
   * @return
   */
  public static String buildReadMeText() {
    StringBuilder readMeStr = new StringBuilder();
    readMeStr.append("AIRLINE FLIGHT MANAGEMENT PROGRAM\n\n");
    readMeStr.append("This program allows you to create a new airline with a flight. Once the first airline is created it will persist\n");
    readMeStr.append("and that same airline name must be used to add flights to the airline. The left-side form has fields to add a flight\n");
    readMeStr.append("with the airline's name, and the the flight's: number, flight source, departure date/time, flight destination, and arrival\n");
    readMeStr.append("date/time.\n\n");
    readMeStr.append("Adding A Flight\n");
    readMeStr.append("Criteria for a successful flight addition:\n");
    readMeStr.append("(1) The airline name must match the name on the server\n");
    readMeStr.append("(2) The flight number must be a valid integer\n");
    readMeStr.append("(3) A valid date must be in the \"mm/dd/yyyy\" format where only integers are allowed\n");
    readMeStr.append("(4) A flight's arrival date/time must be not precede that flight's departure date/time\n");
    readMeStr.append("(5) A flight cannot be duplicated to another one with the exact flight descriptions\n\n");
    readMeStr.append("Searching for Flights\n");
    readMeStr.append("The table lists all of the flights that are either linked to this airline or the search results from the search bar.\n");
    readMeStr.append(SEARCH_USAGE);
    readMeStr.append("\nDeleting a Flight\n");
    readMeStr.append("To a delete a flight, a flight must be highlighted on table of flights. If the one and only flight for an airline is\n");
    readMeStr.append("then **the airline itself will be deleted as well**. A new airline will be created the next time a new flight is added.");
    return readMeStr.toString();
  }

  /**
   * This method builds the usage information about the search command features of the program
   * @return
   */
  private static String buildSearchFlightUsage() {
    StringBuilder usage = new StringBuilder();
    usage.append("You can search flights by:\n");
    for (String str : SEARCH_COMMANDS_LIST) {
      usage.append(str);
      if (str.equals("flight:#")) {
        usage.append(" -- flights with this flight number (valid integer)\n");
      }
      else if (str.equals("src:ABC")) {
        usage.append(" -- flights with this source 3-letter airport code\n");
      }
      else if (str.equals("dest:XYZ")) {
        usage.append(" -- flights with this destination 3-letter airport code\n");
      }
      else {
        usage.append(" -- flights that have both the following source and destination 3-letter airport codes\n");
      }
    }
    usage.append("**NOTE: a blank search displays all of the airline's flights**\n");
    return usage.toString();
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
