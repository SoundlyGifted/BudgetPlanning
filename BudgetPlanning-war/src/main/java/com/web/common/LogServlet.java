
package com.web.common;

import com.ejb.common.OperationResultLogLocal;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LogServlet Servlet processes commands that come from user form of the
 * Application Log on each page of the application.
 */
@WebServlet(name = "LogServlet", urlPatterns = {"/LogServlet"})
public class LogServlet extends HttpServlet {

    @EJB
    private OperationResultLogLocal log;    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String currentDateTime = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]")
                .format(Calendar.getInstance().getTime());
        
        HttpSession session = request.getSession();
        
        // Refreshing the page.
        if (request.getParameter("refresh") != null) {
            log.add(session, currentDateTime + " Awaiting for user command...");
            String pageName = request.getParameter("pageName");
            if (pageName == null || pageName.trim().isEmpty()) {
                pageName = "index.jsp";
            } 
            request.getRequestDispatcher(pageName).forward(request, response);
        }
        
        // Clearing System message log.
        if (request.getParameter("clearLog") != null) {
            log.clear(session);
            log.add(session, "Awaiting for initial user command...");
                        String pageName = request.getParameter("pageName");
            if (pageName == null || pageName.trim().isEmpty()) {
                pageName = "index.jsp";
            }
            request.getRequestDispatcher(pageName).forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
