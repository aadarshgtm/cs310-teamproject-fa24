package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Punch;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import com.github.cliftonlabs.json_simple.Jsoner;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;



public final class DAOUtility {
    public static int calculateTotalMinutes(ArrayList<Punch> punchList, Shift shift) {
        int totalMin = 0;
        int dailyTotalMin = 0;
        boolean isWeekend = false;
        int firstDay = punchList.get(0).getAdjustedtimestamp().getDayOfMonth();
        int lastDay = punchList.get(punchList.size() - 1).getAdjustedtimestamp()
                .getDayOfMonth();

        for (int currentDay = firstDay; currentDay <= lastDay; currentDay++) {

            ArrayList<Punch> dailyPunches = new ArrayList<>();
            for (Punch punch : punchList) {
                if (punch.getOriginaltimestamp().getDayOfMonth() == currentDay) {
                    dailyPunches.add(punch);
                }
            }

            // Calculate daily total minutes
            for (Punch punch : dailyPunches) {                
                // Check if it's the weekend
                DayOfWeek day = punch.getAdjustedtimestamp().getDayOfWeek();
                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                    isWeekend = true;
                }
            }

            // Calculate total minutes for the day
            if (!dailyPunches.isEmpty()) {
                dailyTotalMin = (int) ChronoUnit.MINUTES.between(
                        dailyPunches.get(0).getAdjustedtimestamp(),
                        dailyPunches.get(dailyPunches.size() - 1)
                        .getAdjustedtimestamp());                
                // Apply lunch duration deduction if necessary
                if (!isWeekend) {
                    if (dailyPunches.size() > 2 || dailyTotalMin >= shift.getLunchThreshold()) {
                        dailyTotalMin -= shift.getLunchDuration();
                    }
                }
            }
            totalMin += dailyTotalMin;
        }
        return totalMin;
    }
    
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist) {
        ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();
        
        try {
            for (Punch p : dailypunchlist) {
                HashMap<String, String> punchMap = new HashMap<>();
                
                // Format dates using helper method
                String originalDate = formatTimestamp(p.getOriginaltimestamp());
                String adjustedDate = formatTimestamp(p.getAdjustedtimestamp());
                
                // Add to map
                punchMap.put("originaltimestamp", originalDate);
                punchMap.put("badgeid", p.getBadgeBadge().getId());
                punchMap.put("adjustedtimestamp", adjustedDate);
                punchMap.put("adjustmenttype", p.getAdjustmenttype().toString());
                punchMap.put("terminalid", String.valueOf(p.getTerminalid()));
                punchMap.put("id", String.valueOf(p.getId()));
                punchMap.put("punchtype", p.getPunchtype().toString());
                
                jsonData.add(punchMap);
            }
            
            // Serialize and then escape slashes in the final JSON string
            String jsonString = Jsoner.serialize(jsonData);
            return jsonString.replace("/", "\\/");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating JSON data: " + e.getMessage());
        }
    }
    
    // Helper method to format timestamps using DateTimeFormatter
    private static String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
        return timestamp.format(formatter).toUpperCase();
    }
    
    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchlist, Shift shift) {
        
        // Calculate total scheduled minutes for the pay period
        int scheduledMinutes = shift.getShiftDuration() - shift.getLunchDuration();

        // Total accrued time in minutes within the pay period from punches
        int totalAccruedMinutes = 0;

        for (int i = 0; i < punchlist.size(); i += 2) {
            Punch punchIn = punchlist.get(i);
            Punch punchOut = (i + 1 < punchlist.size()) ? punchlist.get(i + 1) : null;

            if (punchOut != null) {
                LocalDateTime inTime = punchIn.getAdjustedtimestamp();
                LocalDateTime outTime = punchOut.getAdjustedtimestamp();

                // Calculate the difference in minutes between the in and out punches
                int durationMinutes = (int) ChronoUnit.MINUTES.between(inTime, outTime);

                // Subtract lunch duration if within shift hours
                if (!inTime.toLocalTime().isAfter(shift.getLunchStart()) &&
                    !outTime.toLocalTime().isBefore(shift.getLunchEnd())) {
                    durationMinutes -= shift.getLunchDuration();
                }

                totalAccruedMinutes += durationMinutes;
            }
        }

        // Calculate absenteeism as percentage
        BigDecimal absenteeism = new BigDecimal((scheduledMinutes - totalAccruedMinutes) * 100.0 / scheduledMinutes);
        absenteeism = absenteeism.setScale(2, RoundingMode.HALF_UP);
        
        return absenteeism;
    }
}



