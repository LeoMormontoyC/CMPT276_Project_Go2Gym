package com.project.go2gym.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class InvalidateSessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated();

        // Check if the request is for the login page and if the user is authenticated
        if (session != null && isAuthenticated && "/login".equals(httpRequest.getRequestURI())) {
            session.invalidate(); // Invalidate the session
            httpResponse.sendRedirect("/login?logout"); // Optional: Redirect to avoid filter re-processing
            return;
        }

        chain.doFilter(request, response);
    }
}
