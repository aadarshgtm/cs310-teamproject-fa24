/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

/**
 *
 * @author mishan
 */

import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Punch;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PunchDAO {

    // Prepared SQL statements for finding punch by ID 
    private static final String QUERY_FIND_PUNCH_BY_ID = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_LIST_PUNCHES_BY_BADGE_AND_DATE = 
        "SELECT * FROM event WHERE badgeid = ? AND " +
        "(CAST(timestamp AS DATE) = ? OR " +
        "(CAST(timestamp AS DATE) = DATE_ADD(?, INTERVAL 1 DAY) AND " +
        "TIME(timestamp) < '06:00:00' AND eventtypeid IN (0, 2))) " +
        "ORDER BY timestamp ASC";

    private static final int DEFAULT_PUNCH_ID = 0;
    private static final String INSERT_PUNCH_QUERY = 
    "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_DEPARTMENT_TERMINAL_QUERY = 
    "SELECT terminalid FROM department WHERE id = ?";
    
    private static final String SELECT_EMPLOYEE_DEPARTMENT_QUERY = 
    "SELECT departmentid FROM employee WHERE badgeid = ?";


    private final DAOFactory daoFactory;

    // Constructor initializes DAOFactory
    public PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Finds and returns a Punch object by its ID
    public Punch find(int punchId) {
        Punch punch = null;

        try (Connection connection = daoFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_PUNCH_BY_ID)) {
            
            preparedStatement.setInt(1, punchId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int terminalId = resultSet.getInt(2);
                    String badgeId = resultSet.getString(3);
                    
                    // Get timestamp exactly as stored
                    Timestamp dbTimestamp = resultSet.getTimestamp(4);
                    LocalDateTime originaltimestamp = dbTimestamp.toLocalDateTime();
                    System.out.println("Find timestamp: " + originaltimestamp);
                    
                    int punchtype = resultSet.getInt(5);

                    punch = new Punch(id, terminalId, badgeId, originaltimestamp, punchtype);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }

        return punch;
    }

    // Retrieves a list of Punch objects for a given Badge and date
    public ArrayList<Punch> list(Badge badge, LocalDate date) {
        ArrayList<Punch> punchList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = daoFactory.getConnection();

            if (connection.isValid(0)) {
                preparedStatement = connection.prepareStatement(QUERY_LIST_PUNCHES_BY_BADGE_AND_DATE);
                preparedStatement.setString(1, badge.getId());
                preparedStatement.setDate(2, java.sql.Date.valueOf(date));
                preparedStatement.setDate(3, java.sql.Date.valueOf(date));

                boolean hasResults = preparedStatement.execute();

                if (hasResults) {
                    resultSet = preparedStatement.getResultSet();

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int terminalId = resultSet.getInt("terminalid");
                        String badgeId = resultSet.getString("badgeid");
                        Timestamp timestamp = resultSet.getTimestamp("timestamp");
                        LocalDateTime originaltimestamp = timestamp.toInstant()
                                                                   .atZone(ZoneId.systemDefault())
                                                                   .toLocalDateTime();
                        int eventtypeid = resultSet.getInt("eventtypeid");

                        Punch punch = new Punch(id, terminalId, badgeId, originaltimestamp, eventtypeid);
                        punchList.add(punch);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            closeResources(resultSet, preparedStatement);
        }

        return punchList;
    }
    
    // New method to retrieve punches within a date range
    public ArrayList<Punch> list(Badge badge, LocalDate startDate, LocalDate endDate) {
        ArrayList<Punch> punchList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM event WHERE badgeid = ? AND " +
                       "CAST(timestamp AS DATE) BETWEEN ? AND ? " +
                       "ORDER BY timestamp ASC";

        try {
            Connection connection = daoFactory.getConnection();

            if (connection.isValid(0)) {
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, badge.getId());
                preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
                preparedStatement.setDate(3, java.sql.Date.valueOf(endDate));

                boolean hasResults = preparedStatement.execute();

                if (hasResults) {
                    resultSet = preparedStatement.getResultSet();

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int terminalId = resultSet.getInt("terminalid");
                        String badgeId = resultSet.getString("badgeid");
                        Timestamp timestamp = resultSet.getTimestamp("timestamp");
                        LocalDateTime originaltimestamp = timestamp.toInstant()
                                                                  .atZone(ZoneId.systemDefault())
                                                                  .toLocalDateTime();
                        int eventtypeid = resultSet.getInt("eventtypeid");

                        Punch punch = new Punch(id, terminalId, badgeId, originaltimestamp, eventtypeid);
                        punchList.add(punch);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            closeResources(resultSet, preparedStatement);
        }

        return punchList;
}
    

    // Utility method to close ResultSet and PreparedStatement
    private void closeResources(ResultSet resultSet, PreparedStatement... statements) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
        }
        for (PreparedStatement statement : statements) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
    }
    public int create(Punch punch) {
        // Store the original timestamp when create is called
        LocalDateTime timestamp = punch.getOriginaltimestamp();
        System.out.println("Create timestamp: " + timestamp);

        try (Connection connection = daoFactory.getConnection()) {
            if (!connection.isValid(0)) {
                return DEFAULT_PUNCH_ID;
            }

            // Use the stored timestamp for insert
            return insertPunch(connection, punch, timestamp);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }

    private int insertPunch(Connection connection, Punch punch, LocalDateTime timestamp) throws SQLException {
        int punchId = 0;

        try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_PUNCH_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setInt(1, punch.getTerminalid());
            insertStmt.setString(2, punch.getBadgeBadge().getId());
            
            // Use the exact same timestamp from create
            Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);
            insertStmt.setTimestamp(3, sqlTimestamp);
            insertStmt.setInt(4, punch.getPunchtype().ordinal());

            System.out.println("Insert timestamp: " + timestamp);

            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows == 1) {
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        punchId = generatedKeys.getInt(1);
                    }
                }
            }
        }
        return punchId;
    }

    // Helper method to get department ID
    private int getDepartmentId(Connection connection, String badgeId) throws SQLException {
        try (PreparedStatement employeeStmt = connection.prepareStatement(SELECT_EMPLOYEE_DEPARTMENT_QUERY)) {
            employeeStmt.setString(1, badgeId);
            try (ResultSet resultSet = employeeStmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("departmentid");
                }
            }
        }
        return 0; 
    }

    // Helper method to get terminal ID for a department
    private int getDepartmentTerminalId(Connection connection, int departmentId) throws SQLException {
        try (PreparedStatement departmentStmt = connection.prepareStatement(SELECT_DEPARTMENT_TERMINAL_QUERY)) {
            departmentStmt.setInt(1, departmentId);
            try (ResultSet resultSet = departmentStmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("terminalid");
                }
            }
        }
        return 0; 
    }
    /*
    private void closeResources(ResultSet resultSet, PreparedStatement... statements) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
        }
        for (PreparedStatement statement : statements) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
  
    }
    */


}
