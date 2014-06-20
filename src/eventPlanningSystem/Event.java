/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventPlanningSystem;

import java.text.DateFormatSymbols;

/**
 * The Event class handles all the event properties with the respective
 * accessors and mutators.
 *
 * @author jr11acl
 */
public class Event {

    private int day;
    private int month;
    private String name;
    private int startTime, endTime;
    private final DateFormatSymbols cal;
    private final String[] monthNames;

    /**
     * The Constructor for the Event class.
     *
     * @param day The day of the month
     * @param month The month of the event
     * @param name The name of the event
     * @param startTime The starting time of the event
     * @param endTime The finishing time of the event
     */
    public Event(int day, int month, String name, int startTime, int endTime) {
        this.day = day;
        this.month = month;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;

        cal = new DateFormatSymbols();
        monthNames = cal.getMonths();
    }

    /**
     * Returns the day of the month.
     *
     * @return the day of the month
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the month of the event as an integer. E.g. January = 0, February
     * = 1
     *
     * @return The month of the event
     */
    public int getMonth() {
        return month;
    }

    /**
     * Returns a String representation of the month. E.g. 0 = January, 1 =
     * February
     *
     * @return The month of the event as a string
     */
    public String getMonthAsString() {
        return monthNames[this.getMonth()];
    }

    /**
     * Returns the name of the event.
     *
     * @return The name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Returns The start time of the event.
     *
     * @return The start time of the event
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Returns the finishing time of the event.
     *
     * @return The finishing time of the event
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Returns the duration of the event.
     *
     * @return The duration of the event
     */
    public int getDuration() {
        return endTime - startTime;
    }

    /**
     * Returns a String representation of both the start time and the finish
     * time of the event.
     *
     * @return The start and end time of the event
     */
    public String getTimeAsString() {
        if (startTime == 0 && endTime == 24) {
            return "All day";
        } else {
            return String.format("%02d:00", startTime)
                    + "-" + String.format("%02d:00", endTime);
        }
    }

    /**
     * Sets the day of the event.
     *
     * @param day The day of the event
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Sets the month of the event.
     *
     * @param month The month of the event.
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Sets the name of the event.
     *
     * @param name The name of the event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the startTime of the event.
     *
     * @param time The start time of the event.
     */
    public void setStartTime(int time) {
        this.startTime = time;
    }

    /**
     * Sets the finishing time of the event.
     *
     * @param time The finishing time of the event.
     */
    public void setEndTime(int time) {
        this.endTime = time;
    }

    @Override
    public String toString() {
        return getName() + " on " + (getDay() + 1) + " " + getMonthAsString() + ", " + getTimeAsString();
    }

    /**
     * Returns the event in an appropriate format to write to a file, with all
     * the attributes separated by commas (",").
     *
     * @return The string representation of the event
     */
    public String toFileString() {
        return getDay() + "," + getMonth() + "," + getName() + "," + getStartTime() + "," + getEndTime();
    }
}
