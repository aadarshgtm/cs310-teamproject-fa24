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

    // Prepared SQL statements for finding shift by ID or badge
    private static final String QUERY_FIND_SHIFT_BY_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_SHIFT_BY_BADGE = "SELECT shiftid FROM employee WHERE badge = ?";

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
                preparedStatement.setString(1, badge.getId());

                boolean hasResults = preparedStatement.execute();

                if (hasResults) {

                    resultSet = preparedStatement.getResultSet();
                    shiftId = resultSet.getInt("shiftid"); // Get shift ID associated with the badge

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
    public HashMap<Integer, String> resultSetToHashMap(ResultSet resultSet) {

        HashMap<Integer, String> shiftDataMap = new HashMap<>();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (resultSet.next()) {

                // Iterate over each column and store data in the map
                for (int i = 1; i <= numberOfColumns; i++) {
                    String columnName = metaData.getColumnName(i);
                    shiftDataMap.put(i - 1, resultSet.getString(columnName)); // Store column data with index key
                }

            }

        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
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
