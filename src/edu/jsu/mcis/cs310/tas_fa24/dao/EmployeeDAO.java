package edu.jsu.mcis.cs310.tas_fa24.dao;
import edu.jsu.mcis.cs310.tas_fa24.Department;
import edu.jsu.mcis.cs310.tas_fa24.Employee;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import edu.jsu.mcis.cs310.tas_fa24.EmployeeType;
import edu.jsu.mcis.cs310.tas_fa24.Badge;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * EmployeeDAO class for creating employee objects from the database
 * @author Aadarsh
 */

 public class EmployeeDAO {
    private final DAOFactory daoFactory;

    public EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public Employee find(int id) {
        Employee employee = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (Connection connection = daoFactory.getConnection()) {
            String query = "SELECT * FROM employee WHERE id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            boolean hasResults = stmt.execute();

            if (hasResults) {
                rs = stmt.getResultSet();
                if (rs.next()) {
                    employee = mapRowToEmployee(rs);
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
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return employee;
    }

    public Employee find(Badge badge) {
        Employee employee = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (Connection connection = daoFactory.getConnection()) {
            String query = "SELECT id FROM employee WHERE badge_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, badge.getId());
            boolean hasResults = stmt.execute();

            if (hasResults) {
                rs = stmt.getResultSet();
                if (rs.next()) {
                    employee = find(rs.getInt("id"));
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
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return employee;
    }

    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String firstname = rs.getString("firstname");
        String middlename = rs.getString("middlename");
        String lastname = rs.getString("lastname");
        LocalDateTime active = rs.getTimestamp("active").toLocalDateTime();
        DepartmentDAO departmentDAO = new DepartmentDAO(daoFactory);
        Department department = departmentDAO.find(rs.getInt("department_id"));
        ShiftDAO shiftDAO = new ShiftDAO(daoFactory);
        Shift shift = shiftDAO.find(rs.getInt("shift_id"));
        EmployeeType employeeType = EmployeeType.values()[rs.getInt("employee_type")];
        return new Employee(id, firstname, middlename, lastname, active, department, shift, employeeType);
    }
}
