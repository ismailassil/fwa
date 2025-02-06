package School1337.filters;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebFilter(urlPatterns = {"/profile", "/Profile"})
public class UserFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;

        HttpSession session = req.getSession();
        if (session.getAttribute("user") == null) {
            req.getRequestDispatcher("/WEB-INF/jsp/403.jsp").forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}