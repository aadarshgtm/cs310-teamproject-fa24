/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

/**
 *
 * @author mike
 */

import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import java.sql.*;
import java.util.HashMap;

public class ShiftDAO {

    // Prepared SQL statements for finding shift by ID or badgeid
    private static final String QUERY_FIND_SHIFT_BY_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_SHIFT_BY_BADGE = "SELECT shiftid FROM employee WHERE badgeid = ?"; 

    private static final int DEFAULT_SHIFT_ID = 0;

    private final DAOFactory daoFactory;

    // Constructor initializes DAOFactory
    public ShiftDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Finds and returns a Shift object by its ID
    public Shift find(int shiftId) {

        Shift shift = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            Connection connection = daoFactory.getConnection();

            if (connection.isValid(0)) {

                preparedStatement = connection.prepareStatement(QUERY_FIND_SHIFT_BY_ID);
                preparedStatement.setInt(1, shiftId);

                boolean hasResults = preparedStatement.execute();

                if (hasResults) {

                    resultSet = preparedStatement.getResultSet();
                    shift = new Shift(resultSetToHashMap(resultSet)); // Map ResultSet to HashMap for Shift constructor

                }

            }

        } catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            closeResources(resultSet, preparedStatement);

        }

        return shift;

    }

    // Finds and returns a Shift object by Badge
    public Shift find(Badge badge) {

        PreparedStatement preparedStatement = null;
        int shiftId = DEFAULT_SHIFT_ID;
        ResultSet resultSet = null;

        try {

            Connection connection = daoFactory.getConnection();

            if (connection.isValid(0)) {

                preparedStatement = connection.prepareStatement(QUERY_FIND_SHIFT_BY_BADGE);
                preparedStatement.setString(1, badge.getId()); // badgeid is used here

                boolean hasResults = preparedStatement.execute();

                if (hasResults) {

                    resultSet = preparedStatement.getResultSet();
                    if (resultSet.next()) {
                        shiftId = resultSet.getInt("shiftid"); // Get shift ID associated with the badgeid
                    }

                }

            }

        } catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            closeResources(resultSet, preparedStatement);

        }

        return find(shiftId); // Use the shift ID to retrieve the full Shift details
    }

    // Converts a ResultSet into a HashMap that represents the shift data
public HashMap<String, String> resultSetToHashMap(ResultSet resultSet) {
    // HashMap to hold the shift data
    HashMap<String, String> shiftDataMap = new HashMap<>();

    try {
        // Get metadata from the ResultSet
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numberOfColumns = metaData.getColumnCount();

        // Process only the first row of the ResultSet
        if (resultSet.next()) {
            // Iterate over each column
            for (int i = 1; i <= numberOfColumns; i++) {
                String columnName = metaData.getColumnName(i);
                String value = resultSet.getString(columnName); // Retrieve value

                // Check for null value and store accordingly
                if (value != null) {
                    shiftDataMap.put(columnName, value); // Store column data with column name as key
                } else {
                    System.out.println("Warning: Null value found for column: " + columnName);
                }
            }
        } else {
            System.out.println("Warning: No results found in the ResultSet.");
        }

    } catch (SQLException e) {
        throw new DAOException("Error converting ResultSet to HashMap: " + e.getMessage());
    }

    return shiftDataMap;
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