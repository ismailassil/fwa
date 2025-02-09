package School1337.servlets;

import School1337.helper.Database;
import School1337.helper.FileRecord;
import School1337.helper.LoginRecord;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@WebServlet(name = "profileServlet", urlPatterns = "/profile")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        if ( request.getParameter("logout") != null ) {
            session.invalidate();
            response.sendRedirect("/signIn");
            return;
        }

        String email = (String) session.getAttribute("email");

        Properties properties = Database.getUserData(email);

        request.setAttribute("userEmail", properties.getProperty("userEmail"));
        request.setAttribute("fullName", properties.getProperty("fullName"));
        List<String> loginDates = Arrays.asList(properties.getProperty("login_dates").split(","));
        List<String> loginTimes = Arrays.asList(properties.getProperty("login_times").split(","));
        List<String> loginIps = Arrays.asList(properties.getProperty("login_ips").split(","));

        int size = loginDates.size();
        if (loginTimes.size() != size || loginIps.size() != size) {
            System.out.println("Mismatched data length - Login Data");
            return ;
        }
        List<LoginRecord> loginHistory = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            loginHistory.add(new LoginRecord(loginDates.get(i), loginTimes.get(i), loginIps.get(i)));
        }
        request.setAttribute("loginHistory", loginHistory);

        if (properties.getProperty("profileImage") != null)
            request.setAttribute("profileImage", properties.getProperty("profileImage"));

        List<String> imagesName = Arrays.asList(properties.getProperty("images_name").split(","));
        List<String> imagesType = Arrays.asList(properties.getProperty("images_type").split(","));
        List<String> imagesSize = Arrays.asList(properties.getProperty("images_size").split(","));
        size = imagesName.size();
        if (size != imagesType.size() || size != imagesSize.size()) {
            System.out.println("Mismatched data length - Images Data");
            return ;
        }
        List<FileRecord> imageHistory = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            imageHistory.add(new FileRecord(imagesName.get(i), imagesSize.get(i), imagesType.get(i)));
        }

        request.setAttribute("imageHistory", imageHistory);

        request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);

    }
}
