package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Punch;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import com.github.cliftonlabs.json_simple.Jsoner;



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
}



