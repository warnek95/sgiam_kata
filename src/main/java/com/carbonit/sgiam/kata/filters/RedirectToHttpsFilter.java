package com.carbonit.sgiam.kata.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class RedirectToHttpsFilter implements Filter {

    @Value("${server.port}")
    private int httpsPort;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
             FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        if (req.getScheme().equals("http")) {
            String url = "https://" + req.getServerName() + String.format(":%s", httpsPort)
                + req.getContextPath() + req.getServletPath();
            if (req.getPathInfo() != null) {
                url += req.getPathInfo();
            }
            resp.sendRedirect(url);
        } else {
            chain.doFilter(request, response);
        }
    }

}