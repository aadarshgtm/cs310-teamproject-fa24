/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

/**
 *
 * @author Mike
 */
import java.time.*;
import java.util.HashMap;
public class Shift {
    private HashMap<Integer, String> shiftData = new HashMap<>();
    private int shiftDurationMinutes, lunchDurationMinutes;
    private String shiftStartString = null;
    private String shiftEndString = null;
    private String lunchStartString = null;
    private String lunchEndString = null;

    // Constructor initializes the shift data and calculates durations
    public Shift(HashMap<Integer, String> shiftData){
        this.shiftData = shiftData;
        this.shiftDurationMinutes = calculateShiftDuration(shiftData.get(2), shiftData.get(3));
        this.lunchDurationMinutes = calculateLunchDuration(shiftData.get(7), shiftData.get(8)); 
    }

    // Calculates the difference between shift start and end times
    public int calculateShiftDuration(String shiftStart, String shiftEnd){
        LocalTime shiftStartTime = LocalTime.parse(shiftStart);
        LocalTime shiftEndTime = LocalTime.parse(shiftEnd);

        shiftStartString = shiftStartTime.toString();
        shiftEndString = shiftEndTime.toString();

        Duration duration = Duration.between(shiftStartTime, shiftEndTime);
        return (duration.toHoursPart() * 60) + duration.toMinutesPart();
    }

    // Calculates the difference between lunch start and end times
    public int calculateLunchDuration(String lunchStart, String lunchEnd){
        LocalTime lunchStartTime = LocalTime.parse(lunchStart);
        LocalTime lunchEndTime = LocalTime.parse(lunchEnd);

        lunchStartString = lunchStartTime.toString();
        lunchEndString = lunchEndTime.toString();

        Duration duration = Duration.between(lunchStartTime, lunchEndTime);
        return (duration.toHoursPart() * 60) + duration.toMinutesPart();
    }

    // Returns a string representation of the shift and lunch timings
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(shiftData.get(1)).append(": ")
          .append(shiftStartString).append(" - ").append(shiftEndString)
          .append(" (").append(shiftDurationMinutes).append(" minutes); ");

        sb.append("Lunch: ").append(lunchStartString).append(" - ").append(lunchEndString)
          .append(" (").append(lunchDurationMinutes).append(" minutes)");

        return sb.toString();
    }

    // Getter methods for the shift and lunch data
    public HashMap<Integer, String> getShiftData() {
        return shiftData;
    }

    public int getShiftDurationMinutes() {
        return shiftDurationMinutes;
    }

    public int getLunchDurationMinutes() {
        return lunchDurationMinutes;
    }

    public String getShiftStartString() {
        return shiftStartString;
    }

    public String getShiftEndString() {
        return shiftEndString;
    }

    public String getLunchStartString() {
        return lunchStartString;
    }

    public String getLunchEndString() {
        return lunchEndString;
    }

}
