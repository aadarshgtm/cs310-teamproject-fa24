/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;
import java.time.LocalDate;
import java.math.BigDecimal;
import edu.jsu.mcis.cs310.tas_fa24.Absenteeism;
import edu.jsu.mcis.cs310.tas_fa24.Employee;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
/**
 *
 * @author Jeren
 */
public class AbsenteeismDAO {
    public Absenteeism find(Employee employee, LocalDate payPeriodStart) {
        Absenteeism absenteeism = null;
        String query = "SELECT percentage FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employee.getId());
            stmt.setDate(2, Date.valueOf(payPeriodStart));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal percentage = rs.getBigDecimal("percentage");
                absenteeism = new Absenteeism(employee, payPeriodStart, percentage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return absenteeism;
    }
    
    public void create(Absenteeism absenteeism) {
        String query = "REPLACE INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, absenteeism.getEmployee().getId());
            stmt.setDate(2, Date.valueOf(absenteeism.getPayPeriodStart()));
            stmt.setBigDecimal(3, absenteeism.getAbsenteeismPercentage());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
