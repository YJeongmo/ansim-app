package com.example.coderelief.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.coderelief.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private Calendar currentCalendar;
    private List<CalendarDay> calendarDays;
    private int selectedPosition = -1;
    private Set<Integer> daysWithSchedules = new HashSet<>();
    private Map<Integer, List<String>> dayReservationTypes = new HashMap<>();
    
    public void setReservationData(List<Map<String, Object>> reservations) {
        daysWithSchedules.clear();
        dayReservationTypes.clear();
        
        if (reservations != null) {
            for (Map<String, Object> reservation : reservations) {
                String startTime = (String) reservation.get("startTime");
                String appointmentType = (String) reservation.get("appointmentType");
                
                if (startTime != null && startTime.length() >= 10) {
                    try {
                        String dateStr = startTime.substring(0, 10);
                        String[] dateParts = dateStr.split("-");
                        
                        int year = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]) - 1;
                        int day = Integer.parseInt(dateParts[2]);
                        
                        if (year == currentCalendar.get(Calendar.YEAR) && 
                            month == currentCalendar.get(Calendar.MONTH)) {
                            daysWithSchedules.add(day);
                            
                            if (!dayReservationTypes.containsKey(day)) {
                                dayReservationTypes.put(day, new ArrayList<>());
                            }
                            if (appointmentType != null) {
                                dayReservationTypes.get(day).add(appointmentType);
                            }
                        }
                    } catch (Exception e) {
                        // Skip invalid date format
                    }
                }
            }
        }
        generateCalendarDays(); // Regenerate calendar days with updated schedule data
        notifyDataSetChanged();
    }
    
    public CalendarAdapter(Context context, Calendar calendar) {
        this.context = context;
        this.currentCalendar = (Calendar) calendar.clone();
        generateCalendarDays();
    }
    
    @Override
    public int getCount() {
        return calendarDays.size();
    }
    
    @Override
    public Object getItem(int position) {
        return calendarDays.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        }
        
        TextView tvDay = convertView.findViewById(R.id.tv_day);
        TextView tvIndicator = convertView.findViewById(R.id.tv_indicator);
        
        CalendarDay day = calendarDays.get(position);
        
        if (day.dayNumber > 0) {
            tvDay.setText(String.valueOf(day.dayNumber));
            tvDay.setTextColor(day.isCurrentMonth ? Color.BLACK : Color.GRAY);
            
            // Highlight today
            if (day.isToday) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.purple_200));
            } else if (position == selectedPosition) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.purple_500));
                tvDay.setTextColor(Color.WHITE);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            
            // Show schedule indicator with type-based colors
            if (day.hasSchedule) {
                tvIndicator.setVisibility(View.VISIBLE);
                tvIndicator.setText("●");
                
                // Determine color based on reservation types
                List<String> types = dayReservationTypes.get(day.dayNumber);
                int indicatorColor = context.getResources().getColor(R.color.teal_700); // Default
                
                if (types != null && !types.isEmpty()) {
                    String primaryType = types.get(0);
                    switch (primaryType) {
                        case "VISIT":
                            indicatorColor = context.getResources().getColor(R.color.purple_500);
                            break;
                        case "OUTING":
                            indicatorColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                            break;
                        case "OVERNIGHT":
                            indicatorColor = context.getResources().getColor(android.R.color.holo_red_dark);
                            break;
                        case "CONSULTATION":
                            indicatorColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                            break;
                        default:
                            indicatorColor = context.getResources().getColor(R.color.teal_700);
                    }
                }
                
                tvIndicator.setTextColor(indicatorColor);
            } else {
                tvIndicator.setVisibility(View.GONE);
            }
        } else {
            tvDay.setText("");
            tvIndicator.setVisibility(View.GONE);
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        
        return convertView;
    }
    
    public void updateMonth(Calendar newCalendar) {
        this.currentCalendar = (Calendar) newCalendar.clone();
        generateCalendarDays();
        selectedPosition = -1;
        notifyDataSetChanged();
    }
    
    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }
    
    public CalendarDay getSelectedDay() {
        if (selectedPosition >= 0 && selectedPosition < calendarDays.size()) {
            return calendarDays.get(selectedPosition);
        }
        return null;
    }
    
    private void generateCalendarDays() {
        calendarDays = new ArrayList<>();
        
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        Calendar today = Calendar.getInstance();
        
        // Add empty days for previous month
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarDay(0, false, false, false));
        }
        
        // Add days of current month
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            boolean isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                             calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                             calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
            
            // Check if this day has scheduled events from actual reservation data
            boolean hasSchedule = daysWithSchedules.contains(day);
            
            calendarDays.add(new CalendarDay(day, true, isToday, hasSchedule));
        }
        
        // Fill remaining slots to complete 6 weeks (42 days)
        while (calendarDays.size() < 42) {
            calendarDays.add(new CalendarDay(0, false, false, false));
        }
    }
    
    public static class CalendarDay {
        public int dayNumber;
        public boolean isCurrentMonth;
        public boolean isToday;
        public boolean hasSchedule;
        
        public CalendarDay(int dayNumber, boolean isCurrentMonth, boolean isToday, boolean hasSchedule) {
            this.dayNumber = dayNumber;
            this.isCurrentMonth = isCurrentMonth;
            this.isToday = isToday;
            this.hasSchedule = hasSchedule;
        }
    }
}