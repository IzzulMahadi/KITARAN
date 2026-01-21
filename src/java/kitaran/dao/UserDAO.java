package kitaran.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import kitaran.bean.User;
import kitaran.utils.DBConnection;

public class UserDAO {
    public User getUserById(int id) {
        String query = "SELECT * FROM users WHERE id=?";
        User user = null;
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()) {
                user = getUser(rs);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch(SQLException e) {
            System.err.println("Error getting request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return user;
    }
    
    public ArrayList<User> getAllUsers() {
        String query = "SELECT * FROM users";
        ArrayList<User> user = new ArrayList<>();
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) {
                user.add(getUser(rs));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch(SQLException e) {
            System.err.println("Error getting request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return user;
    }
    
    private User getUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setNoPhone(rs.getString("no_phone"));
        return user;
    }
}
