package com.yourname.portal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // 1. Password Encoder: Tells Spring how to hash and verify passwords securely
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Connects our database users to the authentication process
    // 2. Connects our database users to the authentication process
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Pass the userDetailsService directly into the constructor here
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // We still set the password encoder using the setter
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // 3. Define the security rules (Authorization)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabled for simplicity in local development
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to HTML, CSS, and JS files
                        .requestMatchers("/index.html", "/staff.html", "/app.js", "/staff.js", "/login.html").permitAll()

                        // Restrict Admin endpoints
                        .requestMatchers("/api/tasks/assign", "/api/users/available").hasRole("ADMIN")

                        // Restrict Staff endpoints (Update this line)
                        .requestMatchers("/api/tasks/my-tasks", "/api/tasks/*/status", "/api/attendance/**").hasAnyRole("STAFF", "ADMIN")

                        // Require authentication for anything else
                        .anyRequest().authenticated()
                )
                // Use Spring's built-in form login feature
                .formLogin(form -> form
                        .loginPage("/login.html") // Tells Spring to use your custom HTML file
                        .loginProcessingUrl("/login") // The URL your form submits to
                        .defaultSuccessUrl("/index.html", true) // Where to go after success
                        .failureUrl("/login.html?error=true") // Where to go if login fails
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}