package kitaran.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kitaran.bean.Recycle;
import kitaran.bean.User;
import kitaran.dao.RecycleDAO;

public class RecycleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (session == null || user == null) {
            response.sendRedirect("login");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        RecycleDAO recycleDAO = new RecycleDAO();
        Recycle recycle = new Recycle();
        
        int userId = Integer.parseInt(request.getParameter("userId"));
        String type = request.getParameter("type");
        String item = request.getParameter("item");
        String address = request.getParameter("address");
        
        recycle.setUserId(userId);
        recycle.setType(type);
        recycle.setItem(item);
        recycle.setAddress(address);
        
        if(!recycleDAO.createRecycle(recycle)) {
            request.setAttribute("error", "Error creating new request");
            request.getRequestDispatcher("/WEB-INF/jsp/request.jsp").forward(request, response);
        }
        
        response.sendRedirect("dashboard");
        
    }
}
