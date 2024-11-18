package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Punch;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import com.github.cliftonlabs.json_simple.Jsoner;
import java.math.RoundingMode;



public final class DAOUtility {

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
            punchData.put("punchtype", punch.getPunchType1().toString()); // EventType
            punchData.put("adjustmenttype", punch.getAdjustmenttype() != null ? punch.getAdjustmenttype().toString() : "None"); // PunchAdjustmentType
            punchData.put("originaltimestamp", punch.printOriginal());
            punchData.put("adjustedtimestamp", punch.printAdjusted());
            
            // Append the populated HashMap to the ArrayList
            jsonData.add(punchData);
        }
        
        // Serialize the ArrayList to a JSON string and return it
        return Jsoner.serialize(jsonData);
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



