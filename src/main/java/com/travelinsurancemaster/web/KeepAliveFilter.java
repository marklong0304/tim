package com.travelinsurancemaster.web;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class KeepAliveFilter extends OncePerRequestFilter {
   @Override
   protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse,
                                   FilterChain filterChain) throws ServletException, IOException {
      httpServletResponse.addHeader("Connection", "Keep-Alive");
      httpServletResponse.addHeader("Keep-Alive", "timeout=60000");
      filterChain.doFilter(httpServletRequest, httpServletResponse);
   }
}
