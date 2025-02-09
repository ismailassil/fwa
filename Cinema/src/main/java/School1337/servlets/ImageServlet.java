package School1337.servlets;import java.io.*;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Properties;

import School1337.helper.Database;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "ImageServlet", value = "/image")
public class ImageServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (email == null) {
            request.getRequestDispatcher("/WEB-INF/jsp/403.jsp").forward(request, response);
            return;
        }

        String fileName = request.getParameter("filename");
        if (fileName == null || fileName.isEmpty()) {
            request.getRequestDispatcher("/WEB-INF/jsp/400.jsp").forward(request, response);
            return ;
        }
        Properties properties;
        try {
            properties = Database.getImage(email, fileName);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String _status = properties.getProperty("status");
        int status = Integer.parseInt(_status);
        if (status == 1) {
            request.getRequestDispatcher("/WEB-INF/jsp/403.jsp").forward(request, response);
            return ;
        } else if (status == 2) {
            request.getRequestDispatcher("/WEB-INF/jsp/404.jsp").forward(request, response);
            return;
        }
        String file_type = properties.getProperty("file_type");
        String imageBase64 = properties.getProperty("file_data");

        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
        response.getOutputStream().write(imageBytes);
        response.setContentType("image/" + file_type);
        response.getOutputStream().flush();
    }
}