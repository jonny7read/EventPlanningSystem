/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventPlanningSystem;

// Imports
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This is the main class for the Calendar Project.
 *
 * @author jr11acl
 */
public class CalendarForm {

    // Field Declarations
    private static Dimension windowSize;
    private final JFrame frame;
    private final Container container;
    private final JPanel north;
    private final JPanel center;
    private final JPanel south;
    private final JButton btnAdd;
    private final JButton btnEdit;
    private final JButton btnRemove;
    private final JButton btnShowFreeTimeDay;
    private final JButton btnShowFreeTimeWeek;
    private final JButton btnPrevMonth; // button to go to the previous month
    private final JButton btnNextMonth; // button to go to the next month
    private final JButton btnPrevYear; // button to go to the previous year
    private final JButton btnNextYear; // button to go to the next year
    private final JButton btnToday; // button to go to today's date
    private final JButton btnLogout; // logs out and returns to the login form.
    private final JLabel lblCurrentDayText; // 'current day:' text
    private final JLabel lblCurrentDay; // current day selected
    private final JLabel lblCurrentMonth; // current month being displayed
    private final JLabel lblCurrentYear; // current year being displayed       
    private Calendar cal; // calendar instance
    private JLabel lblDayNames; // the short day names of the week ("Mo, Tu, We...")
    private JLabel lblEvents; // 'Events:' text
    private String[] monthNames; // names of the months
    private int today; // todays date
    private int currentMonth; // The current month being displayed
    private int currentYear; // The current year being displayed      
    private int firstDayOfMonth; // The day of the week on which the current month starts.
    // 1 = monday, 2 = tuesday etc..
    private DefaultListModel dayListModel; //list of days - Month?
    private JList eventList; // displays the events
    private JScrollPane scrollPane;
    private JList dayList;  // displays the days
    private Day[] dayArray; // array of Day objects
    private String user;
//    private HashMap<String, Day> dayArray;

    /**
     * Constructor for the main class.
     *
     */
    public CalendarForm() {
        cal = Calendar.getInstance();
        currentMonth = 0;
        today = 0;
        monthNames = new String[10];
        lblCurrentMonth = new JLabel("January");
        lblCurrentYear = new JLabel("2014");
        lblCurrentDay = new JLabel("01");
        lblCurrentDayText = new JLabel("Selected Day:");
        btnPrevMonth = new JButton("<");
        btnNextMonth = new JButton(">");
        btnPrevYear = new JButton("<");
        btnNextYear = new JButton(">");
        btnToday = new JButton("Today");
        btnLogout = new JButton("Log out");
        btnRemove = new JButton("Remove");
        btnEdit = new JButton("Edit");
        btnAdd = new JButton("Add");
        btnShowFreeTimeDay = new JButton("Show free time today");
        btnShowFreeTimeWeek = new JButton("Show free time this week");
        north = new JPanel();
        center = new JPanel();
        south = new JPanel();
        frame = new JFrame("Event Planning System");
        container = frame.getContentPane();
        windowSize = new Dimension(520, 420);
        initComponents();
        readEventsFromFile();
        addComponents();
        buildFrame();
    }

    /**
     * Set relevant components and fields to their initial values.
     *
     */
    private void initComponents() {
        initMonthsArray();
        initDaysArray();
//        dayArray = new HashMap<>();
        eventList = new JList();
        scrollPane = new JScrollPane(eventList);

        Font largeFont = new Font("Verdana", Font.PLAIN, 30);
        Font mediumFont = new Font("Verdana", Font.PLAIN, 20);
        Font smallFont = new Font("Verdana", Font.PLAIN, 15);
        lblCurrentDay.setFont(largeFont);
        lblCurrentMonth.setFont(mediumFont);
        lblCurrentYear.setFont(smallFont);
        lblEvents = new JLabel("Events:");
        lblEvents.setFont(smallFont);
        user = Login.getInstance().getUser();

        currentMonth = cal.get(Calendar.MONTH);
        today = cal.get(Calendar.DAY_OF_MONTH);
        currentYear = cal.get(Calendar.YEAR);
        firstDayOfMonth = 0;

        lblCurrentMonth.setText(monthNames[currentMonth]);
        lblCurrentYear.setText(Integer.toString(currentYear));

        updateDayListModel();

        dayList = new JList(dayListModel);
        dayList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        dayList.setMinimumSize(new Dimension(150, 120));
        dayList.setFixedCellWidth(21);
        dayList.setFixedCellHeight(20);
        dayList.setVisibleRowCount(-1);
        dayList.setSelectedIndex((today - 1) + (firstDayOfMonth - 1));
        dayList.setCellRenderer(new DayListRenderer(firstDayOfMonth, currentMonth, this));

        lblCurrentDay.setText(String.format("%02d", today));

        btnRemove.setEnabled(false);
        btnEdit.setEnabled(false);
    }

    private void readEventsFromFile() {
        int eventsLoaded = 0;
        try {
            FileReader fr = new FileReader("User Accounts/" + user + "/events.txt");
            // try-with-resources
            try (BufferedReader br = new BufferedReader(fr)) {
                String line;

                while ((line = br.readLine()) != null) {
                    // Create an array and store each part of the event
                    // using the commas to split.
                    String[] parts = line.split(",");

                    // initialise variables
                    int dayNum, month, startTime, endTime;
                    String name;

                    // Save the data to variables for use below
                    dayNum = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]);
                    name = parts[2];
                    startTime = Integer.parseInt(parts[3]);
                    endTime = Integer.parseInt(parts[4]);

                    // Create the Event and Day from the data read in from file
                    Event event = new Event(dayNum, month, name, startTime, endTime);
                    Day day = getDay(dayNum, month);

                    // Add the event and increment added events
                    day.addEvent(event);
                    eventsLoaded++;

                    // Update eventList if the event is today
                    int selectedDay = ((dayList.getSelectedIndex() + 1) - firstDayOfMonth);
                    if (selectedDay == day.getDay() - 1 && currentMonth == day.getMonth()) {
                        updateEventListModel(day.getEventList());
                    }
                }
            }
        } catch (IOException ex) {
            // User has logged in for the first time so has no events.txt file.
        }
        if (eventsLoaded == 1) {
            System.out.println(eventsLoaded + " event loaded.");
        } else {
            System.out.println(eventsLoaded + " events loaded.");
        }
    }

    private void writeEventsToFile() {
        int eventsSaved = 0;
        try {
            FileWriter fw = new FileWriter("User Accounts/" + user + "/events.txt", false);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                for (Day day : dayArray) {
                    HashMap map = day.getEvents();
                    Iterator<Entry<String, Event>> it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Event e = it.next().getValue();

                        bw.write(e.toFileString());
                        bw.newLine();
                        eventsSaved++;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CalendarForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (eventsSaved == 1) {
            System.out.println(eventsSaved + " event saved.");
        } else {
            System.out.println(eventsSaved + " events saved.");
        }
    }

    private int getFirstDayOfMonth() {
        cal.set(Calendar.YEAR, currentYear);
        cal.set(Calendar.MONTH, currentMonth);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        if (dayNum == 1) {
            dayNum = 7;
        } else {
            dayNum -= 1;
        }

        return dayNum;
    }

    /**
     * Updates the DefaultListModel for the dayList JList and resets the
     * ListCellRenderer.
     *
     */
    public void updateDayListModel() {
        dayListModel = new DefaultListModel();
        firstDayOfMonth = getFirstDayOfMonth();

        for (int i = (firstDayOfMonth - 1); i > 0; i--) {
            dayListModel.addElement("  ");
        }

        int daysInMonth = getDays(currentMonth);
        for (int i = 0; i <= daysInMonth - 1; i++) {
            int day = dayArray[i].getDay();
            String dayString = String.format("%02d", (day));
            dayListModel.addElement(dayString);
        }

        if (dayList != null) {
            dayList.setCellRenderer(new DayListRenderer(firstDayOfMonth, currentMonth, this));
        }
    }

    /**
     * Initialises the 'monthsArray' array which stores the names of the months
     * as strings.
     */
    private void initMonthsArray() {
        DateFormatSymbols monthStrings = new DateFormatSymbols();
        monthNames = monthStrings.getMonths();
    }

    /**
     * Initialises the 'daysArray' array which stores the days of the month as
     * Day objects.
     */
    private void initDaysArray() {
        dayArray = new Day[372];
        int count = 0;
        for (int month = 0; month <= 11; month++) {
            for (int day = 0; day < 31; day++) {
                dayArray[count++] = new Day((day + 1), month);
            }
        }
    }

    /**
     * Adds all components to their respective panels, then adds the panels to
     * the content pane.
     */
    private void addComponents() {
        createNorthPanel();
        createCenterPanel();
        createSouthPanel();
        addPanelsToContainer();
    }

    /**
     * Returns the number of days in the given month.
     *
     * @param month The currently selected month
     * @return The number of days in month
     */
    private int getDays(int month) {
        int days; // days in month
        int[] longMonths = {0, 2, 4, 6, 7, 9, 11}; // months with 31 days
        boolean monthIsLong = false; // true if month has 31 days
        boolean isLeapYear; // true if month is a leap year

        // if month is in the array, it is long (31 days).
        for (int m = 0; m <= longMonths.length - 1; m++) {
            if (month == longMonths[m]) {
                monthIsLong = true;
                break;
            } else {
                monthIsLong = false;
            }
        }

        // Is the current year a leapYear
        if (currentYear % 4 == 0) {
            if (currentYear % 100 == 0) {
                if (currentYear % 400 == 0) {
                    //divisible by 4, 100 and 400. e.g. 2000
                    isLeapYear = true;
                } else {
                    //divisible by 4 and 100 but not 400, e.g. 1900
                    isLeapYear = false;
                }
            } else {
                //divisible by 4 but not 100, e.g. 2012
                isLeapYear = true;
            }
        } else {
            //not divisible by 4, e.g. 2014
            isLeapYear = false;
        }

        if (monthIsLong) {
            days = 31;
        } else if (month == 1) { // February            
            if (isLeapYear) {
                days = 29;
            } else {
                days = 28;
            }
        } else {
            days = 30;
        }
        return days;
    }

    /**
     * returns the day object with specified parameters
     *
     * @param dayNum
     * @param month
     * @return
     */
    public Day getDay(int dayNum, int month) {
//        Day day = null;
//        if (dayArray.containsKey(dayNum+""+month)){
//            System.out.println("got " + dayNum + " of " + monthNames[month - 1]);
//            day = dayArray.get(dayNum+""+month);
//        } else { 
//            // day has no events
//        }
//        return day;

        return dayArray[dayNum + 31 * month];
    }

    private void createNorthPanel() {
        JLabel lblUser = new JLabel("Logged in as: '" + user + "'");
        north.add(lblUser, BorderLayout.WEST);
        north.add(btnLogout, BorderLayout.EAST);
    }

    /**
     * Adds the components to the centre panel.
     *
     */
    private void createCenterPanel() {
        ListSelectionListener dayLSL = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (dayList.getSelectedIndex() == -1) {
                    // No selection
                } else if (dayList.getSelectedIndex() < (firstDayOfMonth - 1)) {
                    // selected a spacer item (before the 1st)
                    lblCurrentDay.setText(" ");
                    btnAdd.setEnabled(false);
                    btnShowFreeTimeDay.setEnabled(false);
                    btnShowFreeTimeWeek.setEnabled(false);
                } else {
                    // selected a valid day
                    btnAdd.setEnabled(true);
                    btnShowFreeTimeDay.setEnabled(true);
                    btnShowFreeTimeWeek.setEnabled(true);
                    lblCurrentDay.setText((String) dayList.getSelectedValue());
                    Day day = getDay((dayList.getSelectedIndex() + 1) - (firstDayOfMonth), currentMonth);
                    updateEventListModel(day.getEventList());
//                    System.out.println(dayArray.keySet());
                }
            }
        };
        dayList.addListSelectionListener(dayLSL);

        ListSelectionListener eventLSL = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (eventList.getSelectedIndex() == -1) {
                    // No selection
                    btnRemove.setEnabled(false);
                    btnEdit.setEnabled(false);
                } else {
                    // Selection made, enable buttons
                    btnRemove.setEnabled(true);
                    btnEdit.setEnabled(true);
                }
            }
        };
        eventList.addListSelectionListener(eventLSL);

        GroupLayout layout = new GroupLayout(center);
        center.setLayout(layout);
        lblDayNames = new JLabel(" M    T    W    T     F    S    S");
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lblCurrentYear)
                        .addComponent(lblCurrentMonth)
                        .addComponent(lblDayNames, GroupLayout.Alignment.LEADING)
                        .addComponent(dayList))
                .addGap(20)
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCurrentDayText)
                                .addGap(20)
                                .addComponent(lblCurrentDay))
                        .addComponent(lblEvents)
                        .addComponent(scrollPane))
                .addGap(20));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGap(20)
                .addComponent(lblCurrentYear)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lblCurrentMonth)
                        .addComponent(lblCurrentDayText)
                        .addComponent(lblCurrentDay))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lblDayNames)
                        .addComponent(lblEvents))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(dayList)
                        .addComponent(scrollPane))
                .addGap(20));
    }

    /**
     * Adds the components to the south panel.
     *
     */
    private void createSouthPanel() {
        // Button events
        btnPrevMonth.addActionListener(new EventHandler());
        btnNextMonth.addActionListener(new EventHandler());
        btnPrevYear.addActionListener(new EventHandler());
        btnNextYear.addActionListener(new EventHandler());

        btnToday.addActionListener(new EventHandler());

        btnAdd.addActionListener(new EventHandler());
        btnEdit.addActionListener(new EventHandler());
        btnRemove.addActionListener(new EventHandler());
        btnShowFreeTimeDay.addActionListener(new EventHandler());
        btnShowFreeTimeWeek.addActionListener(new EventHandler());

        btnLogout.addActionListener(new EventHandler());

        JLabel lblMonth = new JLabel("Month");
        JLabel lblYear = new JLabel("Year");
        // Button layout
        GroupLayout layout = new GroupLayout(south);
        south.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(btnPrevMonth)
                                .addGap(10)
                                .addComponent(lblMonth)
                                .addGap(10)
                                .addComponent(btnNextMonth))
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(btnPrevYear)
                                .addGap(10)
                                .addComponent(lblYear)
                                .addGap(10)
                                .addComponent(btnNextYear))
                        .addComponent(btnToday, GroupLayout.Alignment.CENTER))
                .addGap(50)
                .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(btnAdd)
                                .addGap(30)
                                .addComponent(btnEdit)
                                .addGap(30)
                                .addComponent(btnRemove))
                        .addComponent(btnShowFreeTimeDay, GroupLayout.Alignment.CENTER)
                        .addComponent(btnShowFreeTimeWeek, GroupLayout.Alignment.CENTER))
                .addGap(20));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(btnPrevMonth)
                        .addComponent(lblMonth, GroupLayout.Alignment.CENTER)
                        .addComponent(btnNextMonth)
                        .addComponent(btnAdd)
                        .addComponent(btnEdit)
                        .addComponent(btnRemove))
                .addGap(10)
                .addGroup(layout.createParallelGroup()
                        .addComponent(btnPrevYear)
                        .addComponent(lblYear, GroupLayout.Alignment.CENTER)
                        .addComponent(btnNextYear)
                        .addComponent(btnShowFreeTimeDay))
                .addGap(10)
                .addGroup(layout.createParallelGroup()
                        .addComponent(btnToday)
                        .addComponent(btnShowFreeTimeWeek))
                .addGap(20));
    }

    /**
     * Adds the panels to the content pane.
     *
     */
    private void addPanelsToContainer() {
        container.add(north, BorderLayout.NORTH);
        container.add(center, BorderLayout.CENTER);
        container.add(south, BorderLayout.SOUTH);
    }

    /**
     * Sets all the frame properties and displays the window.
     *
     */
    private void buildFrame() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setPreferredSize(windowSize);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Please log out", "Don't close here!",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /**
     * Updates the DefaultListModel for the eventsList JList using the given
     * DLM.
     *
     * @param model The DefaultListModel to use.
     */
    public void updateEventListModel(DefaultListModel model) {
        eventList.setModel(model);
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Inner ActionListener class to handle events. Split into separate methods
     * for clarity.
     */
    class EventHandler implements ActionListener {

        private Day day;

        @Override
        public void actionPerformed(ActionEvent event) {
            int monthSpaces = firstDayOfMonth - 1;
            int dayNum = dayList.getSelectedIndex() - monthSpaces;
            if ((dayList.getSelectedIndex() + 1) > monthSpaces) {
                day = getDay(dayNum, (currentMonth)); // because Jan = 0, not 1
//                if (day == null) {
//                    day = new Day(dayNum, (currentMonth));
//                    dayArray.put(dayNum+""+(currentMonth + 1), day);
//                }
//                System.out.println(dayArray.keySet());
                if (event.getSource() == btnAdd) {
                    addNewEvent();
                } else if (event.getSource() == btnEdit) {
                    editEvent();
                } else if (event.getSource() == btnRemove) {
                    removeEvent();
                } else if (event.getSource() == btnShowFreeTimeDay) {
                    showFreeTimeDay(day);
                } else if (event.getSource() == btnShowFreeTimeWeek) {
                    showFreeTimeWeek();
                }
            }
            if (event.getSource() == btnPrevMonth) {
                previousMonth();
            } else if (event.getSource() == btnNextMonth) {
                nextMonth();
            } else if (event.getSource() == btnPrevYear) {
                prevYear();
            } else if (event.getSource() == btnNextYear) {
                nextYear();
            } else if (event.getSource() == btnToday) {
                goToToday();
            } else if (event.getSource() == btnLogout) {
                logout();
            }

        }

        private void addNewEvent() {
            if (day.getFreeTime().substring(0, 1).equals("N")){
                // Day must be all booked up!
                JOptionPane.showMessageDialog(frame, "There is no time available on this day!", 
                        "All booked up!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Create a new form to input the data for the new event
            EventForm eventForm = new EventForm("Add", day.getEventList().size(), day, CalendarForm.this);
            eventForm.setUpComponents();
            // Disable this form
            getFrame().setEnabled(false);
        }

        private void editEvent() {
            // Get the selected item
            int index = eventList.getSelectedIndex();
            // Create a new form to input the new data for the event
            EventForm eventForm = new EventForm("Edit", index, day, CalendarForm.this);
            eventForm.setUpComponents();
            // Disable this form
            getFrame().setEnabled(false);
        }

        private void removeEvent() {
            // Get the selected item
            int index = eventList.getSelectedIndex();
            // Check with user
            int sure = JOptionPane.showConfirmDialog(frame, "Are you sure you want to\nremove the following event: \n\""
                    + day.getEvent(index).getName() + "\"", "WARNING", 0, 2);
            if (sure == 0) {
                // Remove it            
                day.removeEvent(index);
                // Update list of events
                updateEventListModel(day.getEventList());
                // Update list of days
                updateDayListModel();
            } else {
                // Remove is cancelled, so do nothing
            }
        }

        private void showFreeTimeDay(Day day) {
            String freeTime = day.getFreeTime();

            JOptionPane.showMessageDialog(frame, "You're free time available today is:\n" + freeTime,
                    getDateStr(day.getDay(), cal), JOptionPane.INFORMATION_MESSAGE);
        }

        private void showFreeTimeWeek() {
            cal.set(Calendar.YEAR, currentYear);
            cal.set(Calendar.MONTH, currentMonth);
            cal.set(Calendar.DAY_OF_MONTH, day.getDay() - 1);

            int dayNum = cal.get(Calendar.DAY_OF_WEEK) - 1;

            int firstDayNumOfWeek = cal.get(Calendar.DAY_OF_MONTH) - dayNum;

            
            String freeTime = "";
            for (int i = 0; i < 7; i++) {
                Day tempDay = getDay(firstDayNumOfWeek + i, currentMonth);
                String dateStr = getDateStr(tempDay.getDay(), cal);
                freeTime += "<html><u>" + dateStr + ":</u></html>\n" + tempDay.getFreeTime() + "\n";
            }
             // week commencing text (title of JOptionPane)
            String weekCom = getDateStr(getDay(firstDayNumOfWeek, currentMonth).getDay(), cal);
            
            JOptionPane.showMessageDialog(frame, "You're free time available this week is:\n" + freeTime,
                    "Week commencing " + weekCom, JOptionPane.INFORMATION_MESSAGE);
        }

        private String getDateStr(int dayNum, Calendar cal) {
            cal.set(Calendar.DAY_OF_MONTH, dayNum);

            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            String[] dayStrings = DateFormatSymbols.getInstance().getWeekdays();
            String suffix;

            if (dayNum % 10 == 1) {
                suffix = "st";
            } else if (dayNum % 10 == 2) {
                suffix = "nd";
            } else if (dayNum % 10 == 3) {
                suffix = "rd";
            } else {
                suffix = "th";
            }

            String dateStr = dayStrings[dayOfWeek] + " " + dayNum + suffix;

            return dateStr;
        }

        private void previousMonth() {
            int prevMonth = 0;
            // If currently on January, make previous month December and take 1 from year
            if (currentMonth == 0) {
                prevMonth = 11;
                currentYear--;
            } else {
                prevMonth = currentMonth - 1;
            }
            // Display the month and year
            lblCurrentMonth.setText(monthNames[prevMonth]);
            lblCurrentYear.setText(Integer.toString(currentYear));
            // Set current month variable to the previous month.
            currentMonth = prevMonth;
            updateDayListModel();
            dayList.setModel(dayListModel);
            dayList.setSelectedIndex(firstDayOfMonth - 1);
        }

        private void nextMonth() {
            int nextMonth = 0;
            // If currently on December, next month is January and add 1 to year.
            if (currentMonth == 11) {
                nextMonth = 0;
                currentYear++;
            } else {
                nextMonth = currentMonth + 1;
            }
            // Display the month and year
            lblCurrentMonth.setText(monthNames[nextMonth]);
            lblCurrentYear.setText(Integer.toString(currentYear));
            // Set current month variable to the next month.
            currentMonth = nextMonth;
            updateDayListModel();
            dayList.setModel(dayListModel);
            dayList.setSelectedIndex(firstDayOfMonth - 1);
        }

        private void prevYear() {
            // Decrement the year
            currentYear--;
            // Display the new year
            lblCurrentYear.setText(Integer.toString(currentYear));
            updateDayListModel();
            dayList.setModel(dayListModel);
            dayList.setSelectedIndex(firstDayOfMonth - 1);
        }

        private void nextYear() {
            // Increment the year
            currentYear++;
            // Display the new year
            lblCurrentYear.setText(Integer.toString(currentYear));
            updateDayListModel();
            dayList.setModel(dayListModel);
            dayList.setSelectedIndex(firstDayOfMonth - 1);
        }
    }

    private void goToToday() {
        cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH);
        today = cal.get(Calendar.DAY_OF_MONTH);
        currentYear = cal.get(Calendar.YEAR);

        int selection = (today - 1) + (getFirstDayOfMonth() - 1);
        lblCurrentMonth.setText(monthNames[currentMonth]);
        lblCurrentYear.setText(Integer.toString(currentYear));
        updateDayListModel();
        dayList.setModel(dayListModel);
        dayList.setSelectedIndex(selection);
    }

    private void logout() {
        writeEventsToFile();
        Login.getInstance().show();
        frame.setVisible(false);
    }
}
