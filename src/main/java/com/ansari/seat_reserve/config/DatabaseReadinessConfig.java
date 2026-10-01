package com.ansari.seat_reserve.config;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseReadinessConfig {
    @Bean
    HealthIndicator databaseHealth(JdbcTemplate jdbcTemplate) {
        return () -> {
            try { 
                jdbcTemplate.queryForObject("select 1", Integer.class); return Health.up().build(); 
            }
            catch (Exception e) { 
                return Health.down(e).build(); 
            }
        };
    }
}
