/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventPlanningSystem;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author advent
 */
public class EventForm {

    private final JFrame frame;
    private final JTextField txtName;
    private final JLabel lblDay, lblMonth, lblName, lblTime, lblDuration;
    private final JComboBox cmbDay, cmbMonth, cmbTime, cmbDuration;
    private final JButton btnSubmit;
    private final JRadioButton radSelectTime, radAutoTime;
    private final ButtonGroup buttonGroup;
    private final String formType;
    private final int eventID;
    private final Day initialDay;
    private Day chosenDay;
    private final String[] monthNames;
    private final CalendarForm mainForm;
    private final GridBagConstraints gbc;
    private Event newEvent, eventToEdit;
    private final ArrayList<Integer> freeTimes;
    private ArrayList<Integer> usedTimes;

    /**
     * The constructor for the EventForm class
     *
     * @param type Type of form, i.e. "Add", "Edit" or "Remove"
     * @param eventID The ID of the event
     * @param day The day object to which the event will be added
     * @param mainForm The CalendarForm instance
     */
    public EventForm(String type, int eventID, Day day, CalendarForm mainForm) {
        formType = type;
        this.eventID = eventID;
        initialDay = day;
        chosenDay = day;
        this.mainForm = mainForm;
        eventToEdit = day.getEvent(eventID);
        frame = new JFrame();
        lblDay = new JLabel("Day:");
        lblMonth = new JLabel("Month:");
        lblName = new JLabel("Event name:");
        lblTime = new JLabel("Start Time:");
        lblDuration = new JLabel("Duration:");
        cmbDay = new JComboBox();
        cmbMonth = new JComboBox();
        txtName = new JTextField(10);
        radSelectTime = new JRadioButton("Choose Time", true);
        radAutoTime = new JRadioButton("Generate Time", false);
        buttonGroup = new ButtonGroup();
        cmbTime = new JComboBox();
        cmbDuration = new JComboBox();
        btnSubmit = new JButton(type);
        DateFormatSymbols cal = new DateFormatSymbols();
        monthNames = cal.getMonths();
        gbc = new GridBagConstraints();
        freeTimes = new ArrayList();
        usedTimes = new ArrayList();
        txtName.requestFocus();
    }

    /**
     * Initialises all the components, ready to display them correctly on the
     * form. Called from CalendarForm.java.
     */
    public void setUpComponents() {
        for (int i = 1; i <= 31; i++) {
            cmbDay.addItem(String.format("%02d", i));
        }

        for (int i = 0; i <= 11; i++) {
            cmbMonth.addItem(monthNames[i]);
        }

        updateTimeComboBox();

        updateDurationComboBox();

        cmbDay.setSelectedIndex(initialDay.getDay() - 1);
        cmbMonth.setSelectedIndex(initialDay.getMonth());

        if (formType.equals("Edit")) {
            txtName.setText(eventToEdit.getName());
            cmbTime.setSelectedIndex(eventToEdit.getStartTime() - usedTimes.size());
            cmbDuration.setSelectedIndex(eventToEdit.getDuration() - 1);
        }

        cmbTime.addItemListener(new MyItemListener());
        cmbDay.addItemListener(new MyItemListener());
        cmbMonth.addItemListener(new MyItemListener());
        radAutoTime.addActionListener(new TimeHandler());
        radSelectTime.addActionListener(new TimeHandler());

        buttonGroup.add(radSelectTime);
        buttonGroup.add(radAutoTime);

        frame.setLayout(new GridBagLayout());
        gbc.insets = new Insets(5, 5, 5, 5);
        addComponents();

        btnSubmit.addActionListener(new EventHandler());
        frame.getRootPane().setDefaultButton(btnSubmit);

        buildFrame();
    }

    /**
     * Adds the components to the frame.
     */
    private void addComponents() {
        gbc.anchor = GridBagConstraints.EAST;
        addComponent(lblName, 0, 0, 1);
        addComponent(lblDay, 0, 1, 1);
        addComponent(lblMonth, 0, 2, 1);
        addComponent(lblTime, 0, 5, 1);
        addComponent(lblDuration, 0, 6, 1);
        gbc.anchor = GridBagConstraints.WEST;
        addComponent(txtName, 1, 0, 1);
        addComponent(cmbDay, 1, 1, 1);
        addComponent(cmbMonth, 1, 2, 1);
        addComponent(radSelectTime, 1, 3, 1);
        addComponent(radAutoTime, 1, 4, 1);
        addComponent(cmbTime, 1, 5, 1);
        addComponent(cmbDuration, 1, 6, 1);
        gbc.anchor = GridBagConstraints.CENTER;
        addComponent(btnSubmit, 0, 7, 2);
    }

    /**
     * Adds an individual component to the frame with given parameters.
     *
     * @param comp The component to add
     * @param x The x-coordinate in the GridBagLayout at which to place the
     * component
     * @param y The y-coordinate for the above
     * @param width The number of cells the component should take up
     * horizontally
     */
    private void addComponent(Component comp, int x, int y, int width) {
        if (comp.getClass().equals(JButton.class)) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
        }
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        frame.add(comp, gbc);
    }

    /**
     * Sets all the frame properties and displays the window. *
     */
    private void buildFrame() {
        String title = "";
        switch (formType) {
            case "Add":
                title = "Add a new event";
                break;
            case "Edit":
                title = "Edit an existing event";
                break;
            default:
        }
        frame.setTitle(title);
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(300, 350));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // sets the main form to be visible if the event form was exited
                // and not completed.
                mainForm.getFrame().setEnabled(true);
                mainForm.getFrame().setVisible(true);
            }
        });
    }

    /**
     * Calls the getDay method in CalendarForm class
     *
     * @param day The day number
     * @param month The day's month
     * @return The Day object associated with the given day and month
     */
    private Day getDay(int day, int month) {
        return mainForm.getDay(day, month);
    }

    /**
     * Check all the available times to see what the maxDuration should be, then
     * update the duration picker accordingly.
     */
    private void updateDurationComboBox() {
        int maxDuration = 0;
        int maxCount = 0;
        int nextEventTime = 0;
        // for each item in freeTimes
        for (int i = cmbTime.getSelectedIndex() + 1; i < freeTimes.size(); i++) {
            // if the item is sequential to the previous item (5,6,7)
            if (freeTimes.get(i) == (freeTimes.get(i - 1) + 1)) {
                // max duration is incremented
                maxCount++;
                // if this is a new best max
                if (maxCount > maxDuration) {
                    // update the current best
                    maxDuration = maxCount;
                }
            } else {
                // this item is not sequential, reset max
                maxCount = 0;
                // sets the next event time if this is the next event.
                if (nextEventTime == 0) {
                    nextEventTime = freeTimes.get(i - 1) + 1;
                }
            }
        }
        maxDuration += 1; // because freeTime[0] is never checked (can't do i-1)

        if (radSelectTime.isSelected() && freeTimes.size() != 24) {
            int selectedTime = freeTimes.get(cmbTime.getSelectedIndex());
            if (nextEventTime > selectedTime) {
                maxDuration = nextEventTime - selectedTime;
            }
        }

        cmbDuration.removeAllItems();
        cmbDuration.addItem(1 + " hour");
        for (int i = 2; i <= maxDuration; i++) {
            if (i == 24) {
                cmbDuration.addItem("All day");
            } else {
                cmbDuration.addItem(i + " hours");
            }
        }
        if (eventToEdit != null){
            // will be null if adding new event
            cmbDuration.setSelectedIndex(eventToEdit.getDuration() - 1);
        }
    }

    private void updateTimeComboBox() {
        freeTimes.clear();
        if (formType.equals("Add")) {
            usedTimes = chosenDay.getEventTimes();
        } else {
            usedTimes = chosenDay.getEventTimesWithoutEvent(eventToEdit);
        }
        for (int i = 0; i < 24; i++) {
            if (!usedTimes.contains(i)) {
                freeTimes.add(i);
            }
        }

        cmbTime.removeAllItems();
        for (int i : freeTimes) {
            cmbTime.addItem(String.format("%02d:00", i));
        }
        if (eventToEdit != null) {
            // will be null if adding new event
            cmbTime.setSelectedIndex(eventToEdit.getStartTime() - usedTimes.size());
        }
    }

    class EventHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int eventDay = cmbDay.getSelectedIndex();
            int eventMonth = cmbMonth.getSelectedIndex();
            String eventName = txtName.getText();
            int duration;
            int eventTime;

            if (cmbDuration.getSelectedIndex() == 23) {
                duration = 24;
            } else {
                duration = cmbDuration.getSelectedIndex() + 1;
            }

            if (radAutoTime.isSelected()) {
                eventTime = getChosenTime(duration);
                if (eventTime == -1) {
                    return;
                }
            } else {
                eventTime = Integer.parseInt(((String) cmbTime.getSelectedItem()).substring(0, 2));
            }

            // The day at the time of the button click
            chosenDay = getDay(eventDay, eventMonth);

            // Check to see if the eventName is invalid
            if (eventName.startsWith(" ")) {
                JOptionPane.showMessageDialog(frame, "The event name \n\"" + eventName + "\"\nis invalid.\nNames must not start with a space", "Whoops!", 0);
            } else if (eventName.equals("")) {
                JOptionPane.showMessageDialog(frame, "The name field was left blank.\nPlease enter a name for the event.", "Whoops!", 0);
            } else {
                newEvent = new Event(eventDay, eventMonth, eventName, eventTime, eventTime + duration);
                String msg = ""; // The message display in the dialog box
                String action = ""; // What is being done. I.e. Add or Edit
                switch (formType) {
                    case "Add":
                        chosenDay.addEvent(newEvent);
                        action = "Added";
                        msg = "Added event: \"" + newEvent.getName() + "\" to "
                                + (newEvent.getDay() + 1) + " " + newEvent.getMonthAsString()
                                + ", " + newEvent.getTimeAsString();
                        break;
                    case "Edit":
                        boolean dayChanged = false;
                        boolean detailsChanged = false;
                        msg = "Changed event: \"" + eventToEdit.getName() + "\" on "
                                + (eventToEdit.getDay() + 1) + " " + eventToEdit.getMonthAsString()
                                + ", " + newEvent.getTimeAsString();
                        // Display what changed
                        if (eventToEdit.getDay() != newEvent.getDay()) {
                            dayChanged = true;
                            msg += "\nNew day: " + (newEvent.getDay() + 1);
                        }
                        if (eventToEdit.getMonth() != newEvent.getMonth()) {
                            dayChanged = true;
                            msg += "\nNew month: " + newEvent.getMonthAsString();
                        }
                        if (!eventToEdit.getName().equals(eventName)) {
                            detailsChanged = true;
                            msg += "\nNew name: \"" + newEvent.getName() + "\"";
                        }
                        if (eventToEdit.getStartTime() != newEvent.getStartTime()
                                || eventToEdit.getEndTime() != newEvent.getEndTime()) {
                            detailsChanged = true;
                            msg += "\nNew time: " + newEvent.getTimeAsString();
                        }

                        if (!dayChanged && !detailsChanged) {
                            // Event didn't change - do nothing
                            JOptionPane.showMessageDialog(frame, "The event was not changed.", "Huh?", 0);
                            return;
                        } else if (dayChanged) {
                            // Day or month of event has changed
                            // Add to new day and delete from old one
                            chosenDay.addEvent(newEvent);
                            initialDay.removeEvent(eventID);
                        } else if (!dayChanged && detailsChanged) {
                            // Event on the current day has been changed
                            initialDay.editEvent(eventID, newEvent);
                        }
                        action = "Changed";
                        break;
                    default:
                        break;
                }

                mainForm.updateEventListModel(initialDay.getEventList());
                mainForm.updateDayListModel();

                mainForm.getFrame().setEnabled(true);
                mainForm.getFrame().setVisible(true);
                // Close the Form
                frame.dispose();
                JOptionPane.showMessageDialog(mainForm.getFrame(), msg, "Event " + action, 1);

            }
        }

        private int getChosenTime(int duration) {
            String[] timeSlots = getTimesAvailable(duration);
            String selection;

            selection = (String) JOptionPane.showInputDialog(frame, "Which time slot would you like?\n", "Choose a time", JOptionPane.QUESTION_MESSAGE, null, timeSlots, timeSlots[0]);

            if (selection == null) {
                return -1;
            }

            int chosenTime = Integer.parseInt(selection.substring(0, 2));

            return chosenTime;
        }

        private String[] getTimesAvailable(int duration) {
            ArrayList<Integer> timesOkay = new ArrayList();
            //work out how many times to add
            for (int i = 0; i < 24; i++) {
                // if the event doesn't run past midnight
                if ((i + duration) <= 24) {
                    // determine whether we're adding or editing an event
                    // and if this event clashes with any existing events
                    if (formType.equals("Add") && !chosenDay.doesEventOverlap(i, duration)) {
                        timesOkay.add(i);
                    }
                    if (formType.equals("Edit") && !chosenDay.doesExistingEventOverlap(eventToEdit)) {
                        timesOkay.add(i);
                    }
                }
            }

            int nextSlot = 0;
            String[] times = new String[timesOkay.size()];
            for (Integer i : timesOkay) {
                times[nextSlot] = String.format("%02d:00", i) + "-" + String.format("%02d:00", i + duration);
                nextSlot++;
            }

            return times;
        }
    }

    class TimeHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == radAutoTime) {
                cmbTime.setEnabled(false);
                lblTime.setEnabled(false);
                updateTimeComboBox();
                updateDurationComboBox();
            } else if (e.getSource() == radSelectTime) {
                cmbTime.setEnabled(true);
                lblTime.setEnabled(true);
                updateTimeComboBox();
                updateDurationComboBox();
            }
        }
    }

    class MyItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                return;
            }
            if (e.getSource() == cmbTime) {
                updateDurationComboBox();
            } else if (e.getSource() == cmbDay
                    || e.getSource() == cmbMonth) {
                // day has changed
                chosenDay = getDay(cmbDay.getSelectedIndex(), cmbMonth.getSelectedIndex());
                updateTimeComboBox();
                updateDurationComboBox();
            }
        }
    }
}
