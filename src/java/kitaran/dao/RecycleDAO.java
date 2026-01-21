package kitaran.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import kitaran.bean.AdminDTO;
import kitaran.bean.Recycle;
import kitaran.bean.UserDTO;
import kitaran.utils.DBConnection;

public class RecycleDAO {
    
    public Recycle getRecycleById(int id) {
        String query = "SELECT * FROM recycles WHERE id=?";
        Recycle request = null;
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()) {
                request = getRecycle(rs);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch(SQLException e) {
            System.err.println("Error getting request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return request;
    }
    
    public ArrayList<Recycle> getRecycleByUserId(int userId) {
        String query = "SELECT * FROM recycles WHERE user_id=?";
        ArrayList<Recycle> requests = new ArrayList<>();
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(getRecycle(rs));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch(SQLException e) {
            System.err.println("Error getting requests: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }
    
    public ArrayList<Recycle> getAllRecycles() {
        String query = "SELECT * FROM recycles";
        ArrayList<Recycle> requests = new ArrayList<>();
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(getRecycle(rs));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch(SQLException e) {
            System.err.println("Error getting requests: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }
    
    public boolean createRecycle(Recycle recycle) {
        String query = "INSERT INTO recycles (user_id, bin_type, item, address) VALUES (?, ?, ?, ?)";
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, recycle.getUserId());
            pstmt.setString(2, recycle.getType());
            pstmt.setString(3, recycle.getItem());
            pstmt.setString(4, recycle.getAddress());
            
            int rowsInserted = pstmt.executeUpdate();
            
            pstmt.close();
            conn.close();
            
            return rowsInserted > 0;
        } catch(SQLException e) {
            System.err.println("Error creating recycle record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRecycleWeight(int id, double weight) {
        String query = "UPDATE recycles SET weight=? WHERE id=?";
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setDouble(1, weight);
            pstmt.setInt(2, id);
               
            int rowsAffected = pstmt.executeUpdate();
            
            pstmt.close();
            conn.close();
            
            return rowsAffected > 0;
           
        } catch(SQLException e) {
            System.out.println("Error updating recycle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRecycleStatus(int id, String status) {
        String query = "UPDATE recycles SET status=? WHERE id=?";
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
               
            int rowsAffected = pstmt.executeUpdate();
            
            pstmt.close();
            conn.close();
            
            return rowsAffected > 0;
           
        } catch(SQLException e) {
            System.out.println("Error updating recycle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<UserDTO> getUserRecyclingHistory(int userId) {
        String query = "SELECT r.id as recycle_id, r.bin_type, r.item, r.weight, " +
                       "r.status, COALESCE(p.amount, 0) as penalty, p.status as penaltyStatus " +
                       "FROM recycles r " +
                       "LEFT JOIN payments p ON r.id = p.recycle_id " +
                       "WHERE r.user_id = ? " +
                       "ORDER BY r.id DESC";

        ArrayList<UserDTO> history = new ArrayList<>();

        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                UserDTO dto = new UserDTO();
                dto.setRecycleId(rs.getInt("recycle_id"));
                dto.setType(rs.getString("bin_type"));
                dto.setItem(rs.getString("item"));
                dto.setWeight(rs.getDouble("weight"));
                dto.setPenalty(rs.getDouble("penalty"));
                dto.setStatus(rs.getString("status"));
                dto.setPenaltyStatus(rs.getBoolean("penaltyStatus"));

                history.add(dto);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("Error getting user recycling history: " + e.getMessage());
            e.printStackTrace();
        }

        return history;
    }
    
    public ArrayList<AdminDTO> getAdminData(String filter) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT r.id as recycle_id, r.user_id, u.username, r.bin_type, r.item, ")
             .append("r.address, r.weight, r.status as recycle_status, ")
             .append("p.id as payment_id, p.amount, p.status as payment_status, p.bank_name ")
             .append("FROM recycles r ")
             .append("INNER JOIN users u ON r.user_id = u.id ")
             .append("LEFT JOIN payments p ON r.id = p.recycle_id ");

        if (filter != null && !filter.equals("all")) {
            switch (filter) {
                case "pending":
                    query.append("WHERE r.status = 'pending' ");
                    break;
                case "verified":
                    query.append("WHERE r.status = 'verified' ");
                    break;
                case "penalty":
                    query.append("WHERE p.amount > 0 AND (p.status IS NULL OR p.status = 'unpaid') ");
                    break;
            }
        }

        query.append("ORDER BY r.id DESC");

        ArrayList<AdminDTO> results = new ArrayList<>();

        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AdminDTO data = new AdminDTO();
                data.setUserId(rs.getInt("user_id"));
                data.setUsername(rs.getString("username"));
                data.setRecycleId(rs.getInt("recycle_id"));
                data.setType(rs.getString("bin_type"));
                data.setItem(rs.getString("item"));
                data.setAddress(rs.getString("address"));
                data.setWeight(rs.getDouble("weight"));
                data.setRecycleStatus(rs.getString("recycle_status"));

                data.setPaymentId(rs.getInt("payment_id"));
                data.setPenaltyAmount(rs.getDouble("amount"));
                data.setPaymentStatus(rs.getString("payment_status"));
                data.setBankName(rs.getString("bank_name"));

                results.add(data);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("Error getting admin recycle view: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }
    
    public int getTotalRecycleCount() {
        String query = "SELECT COUNT(*) as total FROM recycles";
        int count = 0;

        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("Error getting total recycle count: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }

    public int getPendingRecycleCount() {
        String query = "SELECT COUNT(*) as total FROM recycles WHERE status = 'pending'";
        int count = 0;

        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("Error getting pending recycle count: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }

    public double getTotalSystemWeight() {
        String query = "SELECT SUM(weight) as total FROM recycles WHERE weight > 0";
        double total = 0.0;

        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("Error getting total system weight: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }
    
    public double getTotalWeightByUserId(int id) {
        String query = "SELECT SUM(weight) as total FROM recycles WHERE weight > 0 and user_id=?";
        double total = 0.0;

        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("Error getting total system weight: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }
    
    private Recycle getRecycle(ResultSet rs) throws SQLException {
        Recycle recycle = new Recycle();
        recycle.setId(rs.getInt("id"));
        recycle.setUserId(rs.getInt("user_id"));
        recycle.setType(rs.getString("bin_type"));
        recycle.setItem(rs.getString("item"));
        recycle.setAddress(rs.getString("address"));
        recycle.setStatus(rs.getString("status"));
        recycle.setWeight(rs.getDouble("weight"));
        return recycle;
    }
}
