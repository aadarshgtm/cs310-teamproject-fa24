package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/**
 *
 * @author mishanparajuli
 */

public class Punch {
    private int id;
    private int terminalid;
    private Badge badge;
    private LocalDateTime originaltimestamp;
    private LocalDateTime adjustedtimestamp;
    private int punchtype;
    private EventType pucnchtype1;
    private String adjustmentType; 

   // just use eventType
    public Punch(int terminalid,String badgeId,EventType punchtype1){
        this.terminalid = terminalid;
        this.badge = new Badge(badgeId, "");
        this.punchtype = punchtype;
        
    }
    
    public Punch(int id, int terminalid, String badgeId, LocalDateTime
            originaltimestamp, int punchtype){
        this.id = id; 
        this.terminalid = terminalid;
        this.badge = new Badge(badgeId, "");
        this.originaltimestamp = originaltimestamp;
        this.punchtype =  punchtype;   
        
    }
    public String getAdjustmentType() {
        return adjustmentType;
    }
     public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public int getId() {
        return id;
    }
    public int getPunchType1(){
        return punchtype;
    }

    public int getTerminalid() {
        return terminalid;
    }

    public String getBadge() {
        return badge.getId();
    }
    public Badge getBadgeBadge(){
        return badge;
    }

    public LocalDateTime getOriginaltimestamp() {
        return originaltimestamp;
    }

    public int getPunchtype() {
        return punchtype;
    }

    public LocalDateTime getAdjustedtimestamp() {
        return adjustedtimestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTerminalid(int terminalid) {
        this.terminalid = terminalid;
    }

    public void setBadge(String badgeId) {
        this.badge = new Badge(badgeId, "");
    }

    public void setOriginaltimestamp(LocalDateTime originaltimestamp) {
        this.originaltimestamp = originaltimestamp;
    }

    public void setAdjustedtimestamp(LocalDateTime adjustedtimestamp) {
        this.adjustedtimestamp = adjustedtimestamp;
    }

    public void setPunchtype(int punchtype) {
        this.punchtype = punchtype;
    }
    public void adjust(Shift shift) {
    LocalDateTime adjustedTime = originaltimestamp;

    // Example adjustment logic based on rounding interval
    int roundingInterval = shift.getRoundingInterval();
    int minutes = adjustedTime.getMinute();
    int roundedMinutes = (int) (Math.round((double) minutes / roundingInterval) * roundingInterval);
    adjustedTime = adjustedTime.withMinute(roundedMinutes).withSecond(0);

    // Apply the adjustment
    setAdjustedtimestamp(adjustedTime);

    // Set adjustment type based on the logic applied
    if (roundedMinutes != minutes) {
        adjustmentType = "Interval Round";  // or other appropriate adjustment description
    } else {
        adjustmentType = "None";
    }

    // Here you can also set specific adjustment types based on punch time
    if (adjustedTime.toLocalTime().equals(shift.getShiftStart())) {
        adjustmentType = "Shift Start";
    } else if (adjustedTime.toLocalTime().equals(shift.getLunchStart())) {
        adjustmentType = "Lunch Start";
    } else if (adjustedTime.toLocalTime().equals(shift.getLunchEnd())) {
        adjustmentType = "Lunch Stop";
    } else if (adjustedTime.toLocalTime().equals(shift.getShiftEnd())) {
        adjustmentType = "Shift Stop";
    }
}




    
    public String printOriginal() {
        // Format: #D2C39273 CLOCK IN: WED 09/05/2018 07:00:07
        String badgeId = getBadge();
        String punchTypeVal = "";
        switch (this.punchtype) {
            case 0:
                punchTypeVal = "CLOCK OUT";
                break;
            case 1:
                punchTypeVal = "CLOCK IN";
                break;
            case 2:
                punchTypeVal = "TIME OUT";
                break;
            default:
                System.out.println("Unknown type");
        }

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("EEE MM/dd/yyyy HH:mm:ss")
            .toFormatter(Locale.ENGLISH);

        // Format the timestamp and convert the day of the week to uppercase
        String formattedDateTime = originaltimestamp.format(formatter);
        String dayOfWeekUpper = formattedDateTime.substring(0, 3).toUpperCase();
        formattedDateTime = dayOfWeekUpper + formattedDateTime.substring(3);

        String printOriginalVal = "#" + badgeId + " " + punchTypeVal + ": " + formattedDateTime;
        return printOriginalVal;
    }
    
    public String printAdjusted() {
    if (adjustedtimestamp != null) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("EEE MM/dd/yyyy HH:mm:ss")
            .toFormatter(Locale.ENGLISH);

        // Format the adjusted timestamp and convert the day of the week to uppercase
        String formattedDateTime = adjustedtimestamp.format(formatter);
        String dayOfWeekUpper = formattedDateTime.substring(0, 3).toUpperCase();
        formattedDateTime = dayOfWeekUpper + formattedDateTime.substring(3);

        return formattedDateTime; // Return the formatted adjusted timestamp
    }
    return ""; // Return an empty string if no adjusted timestamp is set
}


    

}
