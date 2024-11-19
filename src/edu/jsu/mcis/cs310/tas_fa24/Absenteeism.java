package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents an employee's absenteeism record for a given pay period 
 */
public class Absenteeism {

    private Employee employee;
    private LocalDate payPeriod;
    private BigDecimal percentage;

    // Date formatter for the expected format (MM-dd-yyyy)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    // Constructor
    public Absenteeism(Employee employee, LocalDate payPeriod, BigDecimal percentage) {
        this.employee = employee;
        this.payPeriod = payPeriod.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY)); // Ensure pay period starts on Sunday
        this.percentage = percentage.setScale(2, RoundingMode.HALF_UP); // Ensure consistent rounding
    }

    // Getter methods
    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getPayPeriod() {
        return payPeriod;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    // toString method for displaying absenteeism info in the expected format
    @Override
    public String toString() {
        return "#" + employee.getBadge().getId() 
               + " (Pay Period Starting " + payPeriod.format(FORMATTER) + "): " 
               + percentage + "%";
    }
}