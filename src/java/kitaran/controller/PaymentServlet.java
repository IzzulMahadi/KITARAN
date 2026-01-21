
package kitaran.controller;

import kitaran.dao.PaymentDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kitaran.bean.Payment;

public class PaymentServlet extends HttpServlet {
    
    private PaymentDAO paymentDAO = new PaymentDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        
        int recycleId = Integer.parseInt(request.getParameter("id"));
        
        Payment payment = paymentDAO.getPaymentByRecycleId(recycleId);
        
        request.setAttribute("payment", payment);
        
        request.getRequestDispatcher("/WEB-INF/jsp/payment.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("requestId"));
        String bankName = request.getParameter("bankName");
        Payment payment = new Payment();
        
        payment.setId(id);
        payment.setBankName(bankName);
        payment.setStatus(true);
        
        if(!paymentDAO.update(payment)) {
            request.setAttribute("error", "Error on payment");
            request.getRequestDispatcher("/WEB-INF/jsp/payment.jsp").forward(request, response);
        }
        response.sendRedirect("dashboard");
    }
}