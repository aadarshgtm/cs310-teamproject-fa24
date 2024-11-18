/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import edu.jsu.mcis.cs310.tas_fa24.Absenteeism;
import edu.jsu.mcis.cs310.tas_fa24.Employee;


/**
 * Data Access Object for handling absenteeism data in the database.
 */
public class AbsenteeismDAO {

    private final Connection connection;

    /**
     * Constructor accepts a DAOFactory to retrieve the database connection.
     *
     * @param factory the DAOFactory instance
     */
    public AbsenteeismDAO(DAOFactory factory) {
        this.connection = factory.getConnection();
    }

    /**
     * Inserts or updates an absenteeism record in the database.
     *
     * @param absenteeism the absenteeism object containing data to store
     */
    public void create(Absenteeism absenteeism) {
        String sql = "REPLACE INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, absenteeism.getEmployee().getId());
            stmt.setDate(2, Date.valueOf(absenteeism.getPayPeriod()));
            stmt.setBigDecimal(3, absenteeism.getPercentage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in AbsenteeismDAO.create: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an absenteeism record for a specific employee and pay period.
     *
     * @param employee  the employee whose record is being retrieved
     * @param payPeriod the pay period for the absenteeism record
     * @return the absenteeism object or null if not found
     */
    public Absenteeism find(Employee employee, LocalDate payPeriod) {
    String sql = "SELECT * FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
    Absenteeism absenteeism = null;

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        // Set parameters
        stmt.setInt(1, employee.getId());
        stmt.setDate(2, Date.valueOf(payPeriod));

        // Debugging: print query and parameters
        System.out.println("Executing query: " + sql);
        System.out.println("Parameters: employeeid = " + employee.getId() + ", payperiod = " + payPeriod);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                BigDecimal percentage = rs.getBigDecimal("percentage");
                absenteeism = new Absenteeism(employee, payPeriod, percentage);
            } else {
                System.out.println("No absenteeism record found for employee: " 
                                   + employee.getId() + ", payPeriod: " + payPeriod);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error in AbsenteeismDAO.find: " + e.getMessage());
        e.printStackTrace();
    }

    return absenteeism;
}
}