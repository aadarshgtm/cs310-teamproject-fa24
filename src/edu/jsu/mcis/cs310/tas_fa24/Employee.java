package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDateTime;
import java.util.Objects;
import java.time.format.DateTimeFormatter;

public class Employee {
    private final int id;
    private final Badge badge;  // Added Badge field
    private final String firstname;
    private final String middlename;
    private final String lastname;
    private final LocalDateTime active;
    private final Department department;
    private final Shift shift;
    private final EmployeeType employeeType;

    // Updated constructor to accept Badge object
    public Employee(int id, Badge badge, String firstname, String middlename, String lastname, LocalDateTime active, Department department, Shift shift, EmployeeType employeeType) {
        this.id = id;
        this.badge = badge;  // Store Badge object
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.active = active;
        this.department = department;
        this.shift = shift;
        this.employeeType = employeeType;
    }

    public int getId() {
        return id;
    }

    public Badge getBadge() {  // Getter for Badge
        return badge;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public LocalDateTime getActive() {
        return active;
    }

    public Department getDepartment() {
        return department;
    }

    public Shift getShift() {
        return shift;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return String.format("ID #%d: %s, %s %s (#%s), Type: %s, Department: %s, Active: %s", 
                             id, lastname, firstname, middlename, badge.getId(), employeeType, department.getdescription(), active.format(formatter));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
