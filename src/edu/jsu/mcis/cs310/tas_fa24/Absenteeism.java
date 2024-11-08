/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;
import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import edu.jsu.mcis.cs310.tas_fa24.Employee;
import edu.jsu.mcis.cs310.tas_fa24.Absenteeism;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
/**
 *
 * @author Jeren
 */
public class AbsenteeismDAO {

    private static final String QUERY_FIND1 = "SELECT * FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
    private static final String QUERY_FIND2 = "INSERT INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?) " +
                                              "ON DUPLICATE KEY UPDATE percentage = VALUES(percentage);";
    
    private final DAOFactory daoFactory;

    // Constructor: Initializes the DAO with a factory for creating connections

    /**
     *
     * @param daoFactory A connection to the database
     */
    public AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Find Employee and their pay period

    /**
     * A find method which retrieves the absenteeism instance from the database
     * @param employee The employee object associated with this absenteeism calculation
     * @param payPeriod The pay period of this absenteeism calculation
     * @return
     */
    public Absenteeism find(Employee employee, LocalDate payPeriod) {

        Absenteeism absenteeism = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get a connection from the DAO factory 
            Connection conn = daoFactory.getConnection();

            // Check if the connection is valid
            if (conn.isValid(0)) {

                // Prepare the SQL Statement
                ps = conn.prepareStatement(QUERY_FIND1); 
                ps.setInt(1, employee.getId()); 
                ps.setDate(2, java.sql.Date.valueOf(payPeriod)); 

                // Execute the query
                boolean hasResults = ps.execute(); 

                if (hasResults) {
                    // Process the result set
                    rs = ps.getResultSet(); 

                    while (rs.next()) { 
                        // Gets current row of the result set to an Absenteeism object
                        BigDecimal percentage = rs.getBigDecimal("percentage");
                        absenteeism = new Absenteeism(employee, payPeriod.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)), percentage);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage()); 
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        } 

        return absenteeism;
    }

    // Create method for Absenteeism using an Absenteeism object

    /**
     * A create method which adds the absenteeism instance to the database
     * @param absenteeism The absenteeism abject to add
     */
    public void create (Absenteeism absenteeism)    {
        
        PreparedStatement ps = null;
         
        try {
            // Get a connection from the DAO factory
            Connection conn = daoFactory.getConnection();

            // Check if the connection is valid
            if (conn.isValid(0)) {
                // Prepare the SQL statement
                ps = conn.prepareStatement(QUERY_FIND2);

                // Set the parameters for the PreparedStatement from the Absenteeism object.
                ps.setInt(1, absenteeism.getEmployee().getId());
                ps.setDate(2, java.sql.Date.valueOf(absenteeism.getPayPeriod())); // Convert LocalDate to java.sql.Date
                ps.setBigDecimal(3, absenteeism.getPercentage());

                // Execute the update.
                ps.executeUpdate();

            
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
    }
    
    // New method to clear absenteeism history for a specific employee (by Merdan)

    /**
     * A clear method which remove an absenteeism instance from the database
     * @param employeeId The employee whose absenteeism is to be removed
     */
    public void clear(int employeeId) {
        PreparedStatement ps = null;

        try {
            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {
                String query = "DELETE FROM absenteeism WHERE employeeid = ?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, employeeId);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
    }
}
