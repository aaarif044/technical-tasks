package com.ansari.seat_reserve.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ansari.seat_reserve.security.BearerTokenFilter;

@Configuration 
public class SecurityConfig {
	@Bean 
	SecurityFilterChain securityFilterChain(HttpSecurity http,BearerTokenFilter filter)throws Exception{
		return http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/actuator/health/**","/actuator/prometheus","/api/v1/health/live","/api/v1/shows").permitAll().anyRequest().authenticated()).addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();
	}
}
