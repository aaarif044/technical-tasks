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
	private static void writeJsonError(jakarta.servlet.http.HttpServletResponse res,int status,String code,String message,jakarta.servlet.http.HttpServletRequest req) throws java.io.IOException {
		res.setStatus(status);
		res.setContentType("application/json");
		res.getWriter().write("{\"code\":"+status+",\"success\":false,\"message\":\""+message+"\",\"data\":{\"error_code\":\""+code+"\",\"path\":\""+req.getRequestURI()+"\"},\"timestamp\":\""+java.time.Instant.now()+"\"}");
	}

	@Bean 
	SecurityFilterChain securityFilterChain(HttpSecurity http,BearerTokenFilter filter)throws Exception{
		return http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/actuator/health/**","/actuator/prometheus","/api/v1/health/live").permitAll().requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/shows","/api/v1/shows/**").permitAll().anyRequest().authenticated()).addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).exceptionHandling(ex->ex
			.authenticationEntryPoint((req,res,e)->writeJsonError(res,401,"UNAUTHORIZED","Authentication required",req))
			.accessDeniedHandler((req,res,e)->writeJsonError(res,403,"FORBIDDEN","Access denied",req)))
		.build();
	}
}
