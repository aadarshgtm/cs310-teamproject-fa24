package edu.jsu.mcis.cs310.tas_fa24.dao;
import edu.jsu.mcis.cs310.tas_fa24.Department;
import java.sql.*;

/**
 * DepartmentDAO class for class for creating department objects from the database
 * @author Jeren
 */
public class DepartmentDAO {
    
    private static final String QUERY_FIND = "Select* FROM department WHERE id=?";
    
    private final DAOFactory daoFactory;
    
    DepartmentDAO (DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    public Department find(int id){
        Department department = null;
        PreparedStatement ps = null;
        ResultSet rs = null; 
        
        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)){
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, id);
                boolean hasResults = ps.execute();
                
                if(hasResults){
                    rs = ps.getResultSet();
                    while (rs.next()){
                        String description = rs.getString("description");
                        int terminalId = rs.getInt ("terminalID");
                        department = new Department (id, description, terminalId); 
                    }
                } 
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null){
                try{
                    rs.close();
                } catch (SQLException e){
                    throw new DAOException(e.getMessage()); 
                }
            }
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException e){
                    throw new DAOException (e.getMessage());
                }
            }
        }
        return department;
    }
}
