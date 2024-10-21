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
        "SELECT * FROM event WHERE badgeid = ? AND CAST(timestamp AS DATE) = ? " +
        "UNION " +
        "SELECT * FROM event WHERE badgeid = ? AND CAST(timestamp AS DATE) = ? " +
        "AND punchtype = 0 LIMIT 1 " +
        "ORDER BY timestamp ASC";

    private static final int DEFAULT_PUNCH_ID = 0;

    private final DAOFactory daoFactory;

    // Constructor initializes DAOFactory
    public PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Finds and returns a Punch object by its ID
    public Punch find(int punchId) {

        Punch punch = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        int id = 0;
        int terminalId = 0;
        String badgeId = "";
        LocalDateTime originaltimestamp = null;
        int punchtype = 0;

        try {

            Connection connection = daoFactory.getConnection();

            if (connection.isValid(0)) {

                preparedStatement = connection.prepareStatement(QUERY_FIND_PUNCH_BY_ID);
                preparedStatement.setInt(1, punchId);

                boolean hasResults = preparedStatement.execute();

                if (hasResults) {
                    resultSet = preparedStatement.getResultSet();
    
                    
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                        terminalId = resultSet.getInt(2);
                        badgeId = resultSet.getString(3);
                        Timestamp timestamp = resultSet.getTimestamp(4);
                        originaltimestamp = timestamp.toInstant()
                                                     .atZone(ZoneId.systemDefault())
                                                     .toLocalDateTime();
                        punchtype = resultSet.getInt(5);

                        punch = new Punch(id, terminalId, badgeId, originaltimestamp, punchtype);
                    }
                }


            }

        } catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            closeResources(resultSet, preparedStatement);

        }

        return punch;

    }

    // Retrieves a list of Punch objects for a given Badge and date
    public List<Punch> list(Badge badge, LocalDate date) {
        List<Punch> punchList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = daoFactory.getConnection();

            if (connection.isValid(0)) {
                preparedStatement = connection.prepareStatement(QUERY_LIST_PUNCHES_BY_BADGE_AND_DATE);
                preparedStatement.setString(1, badge.getId());
                preparedStatement.setDate(2, java.sql.Date.valueOf(date));
                preparedStatement.setString(3, badge.getId());
                preparedStatement.setDate(4, java.sql.Date.valueOf(date.plusDays(1)));

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
                        int punchtype = resultSet.getInt("punchtype");

                        Punch punch = new Punch(id, terminalId, badgeId, originaltimestamp, punchtype);
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
    private void closeResources(ResultSet resultSet, PreparedStatement preparedStatement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
        }
    }

}