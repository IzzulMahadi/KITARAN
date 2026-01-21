package kitaran.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kitaran.bean.User;
import kitaran.bean.AdminDTO;
import kitaran.bean.UserDTO;
import kitaran.dao.RecycleDAO;
import java.util.ArrayList;
import kitaran.dao.PaymentDAO;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        RecycleDAO recycleDAO = new RecycleDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        
        if (user.getIsAdmin()) {
            String filter = request.getParameter("filter");
  
            if (filter == null || filter.isEmpty()) {
                filter = "all";
            }
            
            ArrayList<AdminDTO> data = recycleDAO.getAdminData(filter);
            
            int totalRequests = recycleDAO.getTotalRecycleCount();
            int pendingRequests = recycleDAO.getPendingRecycleCount();
            double totalSystemWeight = recycleDAO.getTotalSystemWeight();
            
            request.setAttribute("data", data);
            request.setAttribute("totalRequests", totalRequests);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("totalSystemWeight", String.format("%.2f", totalSystemWeight));
            request.setAttribute("currentFilter", filter);
            
            request.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(request, response);
            return;
        } else {
            double totalWeight = recycleDAO.getTotalWeightByUserId(user.getId());
            double totalPenalty = paymentDAO.getTotalPaymentByUserId(user.getId());
            
            ArrayList<UserDTO> history = recycleDAO.getUserRecyclingHistory(user.getId());
            
            request.setAttribute("totalWeight", totalWeight);
            request.setAttribute("totalPenalty" , totalPenalty);
            request.setAttribute("history", history);
            
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
            return;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        RecycleDAO recycleDAO = new RecycleDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        
        if (action.equals("verify")) {
            int id = Integer.parseInt(request.getParameter("recycleId"));
            
            if(recycleDAO.updateRecycleStatus(id, "verified")) {
                request.setAttribute("errorMessage", "Error updating status");
            }
            response.sendRedirect("dashboard");
        }
        
        if (action.equals("weight")) {
            int id = Integer.parseInt(request.getParameter("requestId"));
            double weight = Double.parseDouble(request.getParameter("weight"));
            
            if(!recycleDAO.updateRecycleWeight(id, weight)) {
                request.setAttribute("errorMessage", "Error inserting weight");
            }
            response.sendRedirect("dashboard");
        }
        
        if (action.equals("penalty")) {
            double weight = Double.parseDouble(request.getParameter("wrongWeight"));
            double penalty = weight * 0.50;
            
            int userId = Integer.parseInt(request.getParameter("userId"));
            int recycleId = Integer.parseInt(request.getParameter("recycleId"));
            
            kitaran.bean.Payment payment = new kitaran.bean.Payment();
            payment.setUserId(userId);
            payment.setRecycleId(recycleId);
            payment.setAmount(penalty);
            payment.generateRef();
            
            if(!paymentDAO.create(payment)) {
                request.setAttribute("errorMessage", "Error inserting penalty");
            }
            response.sendRedirect("dashboard");
        }
    }
}