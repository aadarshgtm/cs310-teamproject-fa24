package edu.jsu.mcis.cs310.tas_fa24.dao;

import java.sql.*;

public final class DAOFactory {

    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";

    private final String url, username, password;
    
    public DAOFactory(String prefix) {

        DAOProperties properties = new DAOProperties(prefix);

        this.url = properties.getProperty(PROPERTY_URL);
        this.username = properties.getProperty(PROPERTY_USERNAME);
        this.password = properties.getProperty(PROPERTY_PASSWORD);

    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public BadgeDAO getBadgeDAO() {
        return new BadgeDAO(this);
    }
    public ShiftDAO getShiftDAO() {
        return new ShiftDAO(this); // Returns a new DAOShift object
    }
    public PunchDAO getPunchDAO() {
        return new PunchDAO(this); // Returns a new DAOPunch object
    }
    public DepartmentDAO getDepartmentDAO() {
        return new DepartmentDAO(this); // assuming DepartmentDAO uses DAOFactory
    }
    public EmployeeDAO getEmployeeDAO() {
        return new EmployeeDAO(this);
    }
    public AbsenteeismDAO getAbsenteeismDAO() {
        return new AbsenteeismDAO(this);
    }
    
    
}

