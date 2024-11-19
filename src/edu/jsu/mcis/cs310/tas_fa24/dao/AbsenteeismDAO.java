package edu.jsu.mcis.cs310.tas_fa24.dao;

import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import edu.jsu.mcis.cs310.tas_fa24.Absenteeism;
import edu.jsu.mcis.cs310.tas_fa24.Employee;

public class AbsenteeismDAO {

    private Connection connection;

    public AbsenteeismDAO(DAOFactory factory) {
        try {
            this.connection = factory.getConnection(); // Catch any SQLException here
        } catch (SQLException e) {
            handleSQLException("Error establishing database connection in AbsenteeismDAO", e);
        }
    }

    public void create(Absenteeism absenteeism) {
        String sql = "REPLACE INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, absenteeism.getEmployee().getId());
            stmt.setDate(2, Date.valueOf(absenteeism.getPayPeriod()));
            stmt.setBigDecimal(3, absenteeism.getPercentage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Error in AbsenteeismDAO.create", e);
        }
    }

    public Absenteeism find(Employee employee, LocalDate payPeriod) {
        String sql = "SELECT * FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
        Absenteeism absenteeism = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employee.getId());
            stmt.setDate(2, Date.valueOf(payPeriod));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal percentage = rs.getBigDecimal("percentage");
                    absenteeism = new Absenteeism(employee, payPeriod, percentage);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error in AbsenteeismDAO.find", e);
        }

        return absenteeism;
    }

    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}