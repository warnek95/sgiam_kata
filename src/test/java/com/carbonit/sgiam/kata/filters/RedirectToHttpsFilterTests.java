package com.carbonit.sgiam.kata.filters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RedirectToHttpsFilterTests {

    @DisplayName("Redirect the request to https when it is an http request")
    @Test
    void redictedTohttps() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        BDDMockito.given(request.getScheme()).willReturn("http");
        BDDMockito.given(request.getServerName()).willReturn("server");
        BDDMockito.given(request.getContextPath()).willReturn("/");
        BDDMockito.given(request.getServletPath()).willReturn("");

        RedirectToHttpsFilter filter = new RedirectToHttpsFilter();
        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("https://server:0/");
    }

    @DisplayName("Continue if the request is an https request")
    @Test
    void chain() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        BDDMockito.given(request.getScheme()).willReturn("https");

        RedirectToHttpsFilter filter = new RedirectToHttpsFilter();
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
