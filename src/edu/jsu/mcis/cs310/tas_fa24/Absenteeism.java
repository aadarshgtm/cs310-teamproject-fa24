/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal; 

/**
 * Represents an employee's absenteeism record for a given pay period 
 */
public class Absenteeism {
    
    private Employee employee;
    private LocalDate payPeriod;
    private BigDecimal percentage;
    
    // Constructor
    public Absenteeism(Employee employee, LocalDate payPeriod, BigDecimal percentage) {
        this.employee = employee;
        this.payPeriod = payPeriod;
        this.percentage = percentage;
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
        return "#" + employee.getBadge().getId() + " (Pay Period Starting " 
                + payPeriod.toString() + "): " 
                + percentage.setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
    }
}