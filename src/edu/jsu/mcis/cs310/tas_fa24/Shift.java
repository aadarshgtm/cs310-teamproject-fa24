package edu.jsu.mcis.cs310.tas_fa24;

import java.util.HashMap;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Author: Mike Zheng
 */
public class Shift {

    private static final int DEFAULT_DURATION = 0;

    // Fields for shift and break times
    private LocalTime shiftStart, shiftEnd, lunchStart, lunchEnd;
    private int shiftDuration, lunchDuration;

    // HashMap to hold parameters from the shift table
    private HashMap<String, String> shiftDataMap;

    // Formatters
    private DateTimeFormatter inputTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter outputTimeFormat = DateTimeFormatter.ofPattern("HH:mm");

    // Additional shift parameters
    private int roundingInterval, gracePeriod, dockPenalty, lunchThreshold;

    
    public Shift(HashMap<String, String> shiftDataMap) {
        this.shiftDataMap = shiftDataMap;

        // Parse and set shift and lunch times
        parseTimes();

        // Parse additional parameters
        this.dockPenalty = Integer.parseInt(shiftDataMap.get("dockpenalty"));
        this.gracePeriod = Integer.parseInt(shiftDataMap.get("graceperiod"));
        this.roundingInterval = Integer.parseInt(shiftDataMap.get("roundinterval"));
        this.lunchThreshold = Integer.parseInt(shiftDataMap.get("lunchthreshold"));

        // Calculate durations
        this.shiftDuration = calculateDuration(shiftStart, shiftEnd);
        this.lunchDuration = calculateDuration(lunchStart, lunchEnd);
    }

    // Helper method to parse times
    private void parseTimes() {
        this.shiftStart = LocalTime.parse(shiftDataMap.get("shiftstart"), inputTimeFormat);
        this.shiftEnd = LocalTime.parse(shiftDataMap.get("shiftstop"), inputTimeFormat);
        this.lunchStart = LocalTime.parse(shiftDataMap.get("lunchstart"), inputTimeFormat);
        this.lunchEnd = LocalTime.parse(shiftDataMap.get("lunchstop"), inputTimeFormat);
    }

    // Calculate the duration in minutes
    private int calculateDuration(LocalTime start, LocalTime end) {
        if (end.isAfter(start)) {
            return (int) ChronoUnit.MINUTES.between(start, end);
        } else {
            LocalDateTime startDateTime = LocalDateTime.of(2024, 10, 6, start.getHour(), start.getMinute());
            LocalDateTime endDateTime = LocalDateTime.of(2024, 10, 7, end.getHour(), end.getMinute());
            return (int) Duration.between(startDateTime, endDateTime).toMinutes();
        }
    }

    // Override toString() to display shift details
    @Override
    public String toString() {
        return String.format("%s: %s - %s (%d minutes); Lunch: %s - %s (%d minutes)",
                shiftDataMap.get("description"),
                shiftStart.format(outputTimeFormat),
                shiftEnd.format(outputTimeFormat),
                shiftDuration,
                lunchStart.format(outputTimeFormat),
                lunchEnd.format(outputTimeFormat),
                lunchDuration);
    }

    // Getters
    public HashMap<String, String> getShiftDataMap() { return shiftDataMap; }
    public int getShiftDuration() { return shiftDuration; }
    public int getLunchDuration() { return lunchDuration; }
    public LocalTime getShiftStart() { return shiftStart; }
    public LocalTime getShiftEnd() { return shiftEnd; }
    public LocalTime getLunchStart() { return lunchStart; }
    public LocalTime getLunchEnd() { return lunchEnd; }
    public int getRoundingInterval() { return roundingInterval; }
    public int getGracePeriod() { return gracePeriod; }
    public int getDockPenalty() { return dockPenalty; }
    public int getLunchThreshold() { return lunchThreshold; }
}
