package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Punch;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import com.github.cliftonlabs.json_simple.Jsoner;



/**
 * 
 * Utility class for DAOs.  This is a final, non-constructable class containing
 * common DAO logic and other repeated and/or standardized code, refactored into
 * individual static methods.
 * 
 */
public final class DAOUtility {
    
    
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist) {
    // Create ArrayList to store each punch's data
    ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();
    
    for (Punch punch : dailypunchlist) {
        HashMap<String, String> punchData = new HashMap<>();
        
        punchData.put("id", String.valueOf(punch.getId()));
        punchData.put("badgeid", punch.getBadge());
        punchData.put("terminalid", String.valueOf(punch.getTerminalid()));
        
        // Convert punch type to readable string
        String punchTypeString = ""; // Same conversion logic as before
        // ...

        punchData.put("punchtype", punchTypeString); 
        punchData.put("adjustmenttype", punch.getAdjustmentType() != null ? punch.getAdjustmentType() : "None");
        punchData.put("originaltimestamp", punch.printOriginal());
        punchData.put("adjustedtimestamp", punch.printAdjusted());
        
        jsonData.add(punchData);
    }
    
    return Jsoner.serialize(jsonData);
}


}


