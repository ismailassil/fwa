package School1337.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class URLFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        if (!uri.equals("/") && !uri.equals("/signin") && !uri.equals("/signIn") && !uri.equals("/signup") && !uri.equals("/signUp") && !uri.equals("/profile") && !uri.equals("/Profile")) {
            request.getRequestDispatcher("/WEB-INF/jsp/404.jsp").forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
