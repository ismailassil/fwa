package School1337.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.HashSet;

@WebFilter(urlPatterns = "/*")
public class URLFilter implements Filter {
    static private HashSet<String> allowedPaths = new HashSet<>();
    static {
        allowedPaths.add("/");
        allowedPaths.add("/signin");
        allowedPaths.add("/signup");
        allowedPaths.add("/fileupload");
        allowedPaths.add("/profile");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI().toLowerCase();

        if (allowedPaths.contains(uri) || uri.startsWith("/image")) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/jsp/404.jsp").forward(request, response);
        }
    }
}
