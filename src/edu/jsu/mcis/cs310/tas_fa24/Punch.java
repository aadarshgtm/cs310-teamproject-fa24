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
    
    public void adjust(Shift s) {
    LocalDateTime punchTime = this.originaltimestamp;
    LocalDateTime shiftStart = s.getShiftStart();
    LocalDateTime shiftStop = s.getShiftStop();
    LocalDateTime lunchStart = s.getLunchStart();
    LocalDateTime lunchStop = s.getLunchStop();
    int roundInterval = s.getRoundInterval();
    int gracePeriod = s.getGracePeriod();
    int dockPenalty = s.getDockPenalty();
    
    // Adjust for "Shift Start" or "Shift Stop"
    if (punchTime.isBefore(shiftStart) && punchTime.isAfter(shiftStart.minusMinutes(roundInterval))) {
        this.adjustedtimestamp = shiftStart;
        this.adjustmenttype = PunchAdjustmentType.SHIFT_START;
    } else if (punchTime.isAfter(shiftStop) && punchTime.isBefore(shiftStop.plusMinutes(roundInterval))) {
        this.adjustedtimestamp = shiftStop;
        this.adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
    }

    // Adjust for "Lunch Start" and "Lunch Stop"
    else if (punchTime.isAfter(lunchStart) && punchTime.isBefore(lunchStop)) {
        if (this.punchtype == PunchType.CLOCK_OUT) {
            this.adjustedtimestamp = lunchStart;
            this.adjustmenttype = PunchAdjustmentType.LUNCH_START;
        } else if (this.punchtype == PunchType.CLOCK_IN) {
            this.adjustedtimestamp = lunchStop;
            this.adjustmenttype = PunchAdjustmentType.LUNCH_STOP;
        }
    }

    // Adjust for "Grace Period"
    else if (punchTime.isAfter(shiftStart) && punchTime.isBefore(shiftStart.plusMinutes(gracePeriod))) {
        this.adjustedtimestamp = shiftStart;
        this.adjustmenttype = PunchAdjustmentType.GRACE_PERIOD_START;
    } else if (punchTime.isBefore(shiftStop) && punchTime.isAfter(shiftStop.minusMinutes(gracePeriod))) {
        this.adjustedtimestamp = shiftStop;
        this.adjustmenttype = PunchAdjustmentType.GRACE_PERIOD_STOP;
    }

    // Adjust for "Dock Penalty"
    else if (punchTime.isAfter(shiftStart.plusMinutes(gracePeriod)) && punchTime.isBefore(shiftStart.plusMinutes(dockPenalty))) {
        this.adjustedtimestamp = shiftStart.plusMinutes(dockPenalty);
        this.adjustmenttype = PunchAdjustmentType.DOCK_START;
    } else if (punchTime.isBefore(shiftStop.minusMinutes(gracePeriod)) && punchTime.isAfter(shiftStop.minusMinutes(dockPenalty))) {
        this.adjustedtimestamp = shiftStop.minusMinutes(dockPenalty);
        this.adjustmenttype = PunchAdjustmentType.DOCK_STOP;
    }

    // Interval Rounding
    else {
        int minute = punchTime.getMinute();
        int interval = roundInterval;
        int roundedMinutes = (minute + interval / 2) / interval * interval;
        this.adjustedtimestamp = punchTime.withMinute(roundedMinutes).withSecond(0).withNano(0);
        this.adjustmenttype = PunchAdjustmentType.INTERVAL_ROUND;
    }
}
    
    
    public String printAdjusted() {
         String badgeId = getBadge();
            String punchTypeVal = this.adjustmenttype.toString().replace("_", " ");
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("EEE MM/dd/yyyy HH:mm:ss")
                    .toFormatter(Locale.ENGLISH);
            String formattedDateTime = adjustedtimestamp.format(formatter);
            String dayOfWeekUpper = formattedDateTime.substring(0, 3).toUpperCase();
            formattedDateTime = dayOfWeekUpper + formattedDateTime.substring(3);

            return "#" + badgeId + " " + punchTypeVal + ": " + formattedDateTime;
    }

}
