/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventPlanningSystem;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;

/**
 * The Day class includes the event data for a specific day.
 *
 * @author jr11acl
 */
public class Day {

    private final int day;
    private final int month;
    private static int nextEventID;
    private DefaultListModel eventsList; // list of events
    private final HashMap<Integer, Event> allEvents;
    private final DateFormatSymbols cal;
    private final String[] monthNames;

    /**
     * Constructor for the Day class
     *
     * @param day The Day number of the month
     * @param month The month to which this day belongs
     */
    public Day(int day, int month) {
        this.day = day;
        this.month = month;
        this.allEvents = new HashMap();
        eventsList = new DefaultListModel();

        cal = new DateFormatSymbols();
        monthNames = cal.getMonths();
    }

    /**
     * Returns the day of the month.
     *
     * @return The day of the month
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the numerical value of the month. E.g. January = 0, February = 1
     *
     * @return The numerical month variable
     */
    public int getMonth() {
        return month;
    }

    /**
     * Returns a string representation of the month assigned to this day. E.g. 0
     * = January, 1 = February
     *
     * @return A String representation of the month.
     */
    public String getMonthAsString() {
        return monthNames[this.getMonth()];
    }

    /**
     * Returns the DefaultListModel of the events on this day for use in a
     * JList.
     *
     * @return The DefaultListModel object
     */
    public DefaultListModel getEventList() {
        eventsList = new DefaultListModel();
        for (Event e : allEvents.values()) {
            eventsList.addElement(e.getTimeAsString() + " - " + e.getName());
        }

        return eventsList;
    }

    /**
     * Returns the event with given eventID in the list.
     *
     * @param eventID The integer ID for the event (0 = first event)
     * @return The Event object
     */
    public Event getEvent(int eventID) {
        return allEvents.get(eventID);
    }

    /**
     * Returns the HashMap of all events on this day.
     *
     * @return The HashMap of events
     */
    public HashMap getEvents() {
        return allEvents;
    }

    /**
     * Adds the given event to the HashMap allEvents.
     *
     * @param event The event object to add
     */
    public void addEvent(Event event) {
        nextEventID = allEvents.size();
        allEvents.put(nextEventID, event);
    }

    /**
     * Gets the event with given ID and replaces it's values with those of the
     * newEvent argument
     *
     * @param eventID The ID of the event in the list. (0 = first event showing)
     * @param newEvent The new event details to use
     */
    public void editEvent(int eventID, Event newEvent) {
        Event event = getEvent(eventID);
        event.setDay(newEvent.getDay());
        event.setMonth(newEvent.getMonth());
        event.setName(newEvent.getName());
        event.setStartTime(newEvent.getStartTime());
        event.setEndTime(newEvent.getEndTime());
    }

    /**
     * Removes the event with given index from this day.
     *
     * @param index The key mapped to the Event in the HashMap
     */
    public void removeEvent(int index) {
        // Replace space with event below for each item in the list,
        // then remove the last event.
        for (int i = index; i <= eventsList.size() - 1; i++) {
            // Event is in this month
            if (i != (eventsList.size() - 1)) {
                // Not the last event in the list
                // Make the name of the event equal to the name of the next event;	
                allEvents.get(i).setName(allEvents.get(i + 1).getName());
            } else {
                // Event is the last one in the list
                // Remove event
                allEvents.remove(i);
            }
        }
        nextEventID--;
    }

    /**
     * Returns the times of the events on this day in a list.
     *
     * @return An arrayList of event times.
     */
    public ArrayList getEventTimes() {
        ArrayList times = new ArrayList();

        for (Event e : allEvents.values()) {
            for (int i = 0; i < e.getDuration(); i++) {
                times.add(e.getStartTime() + i);
            }
        }

        return times;
    }

    /**
     * Returns the times of the events on this day in a list, EXCEPT for the
     * given event (used for editing purposes).
     *
     * @param eventToMiss The event to skip out when checking times
     * @return An arrayList of event times
     */
    public ArrayList getEventTimesWithoutEvent(Event eventToMiss) {
        ArrayList times = new ArrayList();

        for (Event e : allEvents.values()) {
            if (!e.equals(eventToMiss)) {
                for (int i = 0; i < e.getDuration(); i++) {
                    times.add(e.getStartTime() + i);
                }
            }
        }

        return times;
    }

    /**
     * Returns the free time available on this day, in the format:
     * [startTime-endTime],[startTime-endTime]. If no events have been added to
     * the day, then the user is told they have all day free.
     *
     * @return the free time available on this day.
     */
    public String getFreeTime() {
        ArrayList usedTimes = getEventTimes();
        ArrayList freeTime = new ArrayList();
        String freeTimeStr = "";

        for (int i = 0; i <= 23; i++) {
            if (!usedTimes.contains(i)) {
                freeTime.add(i);
            }
        }

        if (freeTime.size() == 24) {
            return "All day! You have no events!\n";
        } else if (freeTime.isEmpty()) {
            return "No time! All booked up!\n";
        }

        int first = (int) freeTime.get(0);
        int last;

        for (int i = 1; i < freeTime.size(); i++) {
            if ((int) freeTime.get(i) != (int) freeTime.get(i - 1) + 1) {
                last = (int) freeTime.get(i - 1) + 1;
                freeTimeStr += formatTime(first) + "-" + formatTime(last)
                        + " (" + (last - first) + " hours)\n";
                first = (int) freeTime.get(i);
            }
        }
        last = 24;
        freeTimeStr += formatTime(first) + "-" + formatTime(last)
                + " (" + (last - first) + " hours)\n";

        return freeTimeStr;
    }

    private String formatTime(int time) {
        return String.format("%02d:00", time);
    }

    /**
     * Cycles through the event list and checks to see if an event added
     * with the given times would overlap any of the current events in the list.
     *
     * @param startTime start time of the event
     * @param duration duration of the event
     * @return True if it does overlap, else false
     */
    public boolean doesEventOverlap(int startTime, int duration) {
        int endTime = startTime + duration;
        for (Event e : allEvents.values()) {
            if (startTime >= e.getStartTime() && startTime < e.getEndTime() // starts during another event
                    || endTime > e.getStartTime() && endTime <= e.getEndTime() // ends during another event
                    || startTime <= e.getStartTime() && endTime >= e.getEndTime()) { // starts before and ends after another event
                // event overlaps
                return true;
            }
        }
        return false;
    }

    /**
     * Cycles through the event list and checks to see if an event added
     * with the given times would overlap any of the current events in the list,
     * skipping the given event.
     *
     * @param eventToMiss The event to skip when checking event times
     * @return True if it does overlap, else false
     */
    public boolean doesExistingEventOverlap(Event eventToMiss) {
        int startTime = eventToMiss.getStartTime();
        int endTime = eventToMiss.getEndTime();
        for (Event e : allEvents.values()) {
            if (!e.equals(eventToMiss)) {
                if (startTime >= e.getStartTime() && startTime < e.getEndTime() // starts during another event
                        || endTime > e.getStartTime() && endTime <= e.getEndTime() // ends during another event
                        || startTime <= e.getStartTime() && endTime >= e.getEndTime()) { // starts before and ends after another event
                    // event overlaps
                    return true;
                }
            }
        }
        return false;
    }
}
