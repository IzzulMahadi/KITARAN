package kitaran.dao;

import kitaran.bean.Payment;
import kitaran.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;

public class PaymentDAO {
    
    public boolean create(Payment payment) {
        String query = "INSERT INTO payments (user_id, recycle_id, amount, reference) VALUES (?, ?, ?, ?)";
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, payment.getUserId());
            pstmt.setInt(2, payment.getRecyleId());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setString(4, payment.getRef());
            
            int rowsInserted = pstmt.executeUpdate();
            
            pstmt.close();
            conn.close();

            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error creating payment record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Insert new payment
    public boolean update(Payment payment) {
        String query = "UPDATE payments SET bank_name=?, status=?, paydate=? WHERE id=?";
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1, payment.getBankName());
            pstmt.setBoolean(2, payment.getStatus());
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(4, payment.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            pstmt.close();
            conn.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get payment by ID
    public Payment getPaymentById(int id) {
        String query = "SELECT * FROM payments WHERE id=?";
        Payment payment = null;
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                payment = getPayment(rs);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting payment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return payment;
    }
    
    public Payment getPaymentByRecycleId(int recycleId) {
        String query = "SELECT * FROM payments WHERE recycle_id=?";
        Payment payment = null;
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, recycleId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                payment = getPayment(rs);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting payment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return payment;
    }
    
    public double getTotalPaymentByUserId(int id) {
        String query = "SELECT SUM(amount) as total FROM payments WHERE amount > 0 and not status and user_id=?";
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
    
    public ArrayList<Payment> getPaymentsByUserId(int userId) {
        String query = "SELECT * FROM payments WHERE user_id=? ORDER BY paydate DESC";
        ArrayList<Payment> payments = new ArrayList<>();
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                payments.add(getPayment(rs));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting user payments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return payments;
    }
    
    public ArrayList<Payment> getAllPayments() {
        String query = "SELECT * FROM payments ORDER BY paydate DESC";
        ArrayList<Payment> payments = new ArrayList<>();
        
        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery(query);
            
            while (rs.next()) {
                payments.add(getPayment(rs));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting all payments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return payments;
    }
    
    private Payment getPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setUserId(rs.getInt("user_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setBankName(rs.getString("bank_name"));
        payment.setStatus(rs.getBoolean("status"));
        payment.setDate(rs.getTimestamp("paydate"));
        payment.setRef(rs.getString("reference"));
        return payment;
    }
}