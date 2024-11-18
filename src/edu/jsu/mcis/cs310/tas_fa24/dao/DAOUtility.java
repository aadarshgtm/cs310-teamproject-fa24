package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Punch;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import com.github.cliftonlabs.json_simple.Jsoner;
import edu.jsu.mcis.cs310.tas_fa24.Shift;



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
        // Create an ArrayList to store each punch's data
        ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();
        
        // Iterate over each Punch in the list
        for (Punch punch : dailypunchlist) {
            // Create a HashMap to store the data for each punch
            HashMap<String, String> punchData = new HashMap<>();
            
            // Add punch details to the HashMap
            punchData.put("id", String.valueOf(punch.getId()));
            punchData.put("badgeid", punch.getBadge());
            punchData.put("terminalid", String.valueOf(punch.getTerminalid()));
            //punchData.put("punchtype", punch.getPunchType1().toString()); // EventType
            punchData.put("adjustmenttype", punch.getAdjustmenttype() != null ? punch.getAdjustmenttype().toString() : "None"); // PunchAdjustmentType
            punchData.put("originaltimestamp", punch.printOriginal());
            punchData.put("adjustedtimestamp", punch.printAdjusted());
            
            // Append the populated HashMap to the ArrayList
            jsonData.add(punchData);
        }
        
        // Serialize the ArrayList to a JSON string and return it
        return Jsoner.serialize(jsonData);
    }
}



