/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventPlanningSystem;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author advent
 */
public class DayListRenderer extends JLabel implements ListCellRenderer {

    private final int firstDay;
    private final int currentMonth;
    private final CalendarForm mainForm;

    /**
     * The Constructor for the DayListRenderer class which 
     * gets the currently selected month and the first day of
     * the month and stores them to be used later.
     *
     * @param firstDay The first day of the month
     * @param currentMonth The currently selected month
     * @param mainForm The instance of CalendarForm
     */
    public DayListRenderer(int firstDay, int currentMonth, CalendarForm mainForm) {
        super();
        this.firstDay = firstDay;
        this.currentMonth = currentMonth;
        this.mainForm = mainForm;
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText((String) value);
        Day day = null;
        if (index >= firstDay - 1){
            day = mainForm.getDay(index - (firstDay - 1), currentMonth);            
        }        
        if (day == null || day.getEventList().isEmpty()) {
            setForeground(Color.BLACK);
        } else {
            // the day has an event
            setForeground(Color.RED);
        }
        if (isSelected) {
            setBackground(new Color(184,207,229));           
        } else {
            setBackground(Color.WHITE);
        }
        return this;
    }
}
