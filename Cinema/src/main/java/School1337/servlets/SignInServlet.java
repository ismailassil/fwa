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

@WebServlet(name = "signInServlet", urlPatterns = {"/signin", "/signIn"})
public class SignInServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/signIn.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (email.isEmpty()) {
            request.setAttribute("error", "Please enter a valid email");
            doGet(request, response);
        }
        if (password.isEmpty()) {
            request.setAttribute("error", "Please enter a valid password");
            doGet(request, response);
        }

        if (!Database.isUserExists(email, password)) {
            System.out.println("User Already Exists");
            request.setAttribute("error", "Invalid email or password");
            doGet(request, response);
        }

        HttpSession session = request.getSession();
        session.setAttribute("email", email);
        User user = new User(email);
        session.setAttribute("user", user);

        response.sendRedirect("/profile");
    }
}
