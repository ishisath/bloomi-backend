package com.eadcw.FlowerGiftApp.config;

import com.eadcw.FlowerGiftApp.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:8080",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests(authz -> authz
                        // ===== PUBLIC ENDPOINTS =====
                        // Auth endpoints
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register-admin").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/validate-token").permitAll()

                        // Swagger/API docs
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Public product endpoints (anyone can view products)
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                        // ===== ADMIN-ONLY ENDPOINTS =====
                        // Admin product management
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

                        // Reports (admin only)
                        .requestMatchers(HttpMethod.GET, "/reports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/reports/**").hasRole("ADMIN")

                        // Offers (admin only)
                        .requestMatchers(HttpMethod.POST, "/offers").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/offers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/offers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/offers").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/users/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/**").authenticated()


                        /// Order endpoints (authenticated users)
                                .requestMatchers(HttpMethod.POST, "/orders").authenticated()
                                .requestMatchers(HttpMethod.GET, "/orders/{orderId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/orders/user/{userId}").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/orders/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/orders/**").authenticated()

                                .requestMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")

                        // Payment endpoints (authenticated users)
                        .requestMatchers(HttpMethod.POST, "/payments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/payments/**").authenticated()


                        // ===== AUTHENTICATED ENDPOINTS =====
                        // Everything else requires authentication
                        .requestMatchers("/notifications/**").authenticated()

                        // Public offers view
                        .requestMatchers(HttpMethod.GET, "/offers/active").permitAll()

                        .anyRequest().authenticated()
                )
                .httpBasic().disable();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}