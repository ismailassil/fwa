package School1337.servlets;

import School1337.helper.Database;
import School1337.helper.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

@WebServlet(name = "signUpServlet", urlPatterns = {"/signup", "/signUp"})
public class SignUpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Please fill all the required fields");
            doGet(request, response);
        }
        if (!password.equals(confirmPassword)) {

            request.setAttribute("error", "Passwords do not match");
        }
        if (!phoneNumber.matches("\\d{10,}")) {
            request.setAttribute("error", "Invalid phone number");
            doGet(request, response);
        }
        if (phoneNumber.length() < 10) {
            request.setAttribute("error", "Phone number is too short");
            doGet(request, response);
        }

        Properties properties = new Properties();
        properties.setProperty("firstName", firstName);
        properties.setProperty("lastName", lastName);
        properties.setProperty("phoneNumber", phoneNumber);
        properties.setProperty("email", email);
        properties.setProperty("password", password);
        properties.setProperty("login_ip", request.getRemoteAddr());

        if (!Database.insertNewUser(properties)) {
            request.setAttribute("error", "Error");
            doGet(request, response);
        }

        HttpSession session = request.getSession();

        session.setAttribute("email", email);
        session.setAttribute("user", new User(email));

        response.sendRedirect("/profile");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/signUp.jsp").forward(request, response);
    }
}
