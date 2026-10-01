package com.ansari.seat_reserve.security;
import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class BearerTokenFilter extends OncePerRequestFilter {

 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException{
   
  String h=req.getHeader("Authorization");
  
  if(h!=null&&h.startsWith("Bearer ")){
    String token=h.substring(7).trim(); if(!token.isBlank()) SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(token,null,AuthorityUtils.NO_AUTHORITIES));
  }

  chain.doFilter(req,res);
  }
}
