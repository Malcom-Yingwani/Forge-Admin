package za.co.heritagebaptist.forge.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, @Value("${springdoc.api-docs.enabled:false}") boolean openApiEnabled, za.co.heritagebaptist.forge.auth.JwtService jwt, za.co.heritagebaptist.forge.auth.AppUserRepository users) throws Exception {
        http.cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .addFilterBefore(new za.co.heritagebaptist.forge.auth.JwtAuthFilter(jwt, users), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(errors -> errors.authenticationEntryPoint((request, response, exception) -> {
                response.setStatus(401); response.setContentType("application/problem+json");
                response.getWriter().write("{\"status\":401,\"title\":\"Unauthorized\",\"detail\":\"Authentication required\"}");
            }).accessDeniedHandler((request, response, exception) -> {
                response.setStatus(403); response.setContentType("application/problem+json");
                response.getWriter().write("{\"status\":403,\"title\":\"Forbidden\",\"detail\":\"Access denied\"}");
            }))
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll();
                if (openApiEnabled) {
                    auth.requestMatchers(HttpMethod.GET, "/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll();
                }
                auth.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh", "/api/auth/logout", "/api/auth/forgot-password", "/api/auth/reset-password").permitAll();
                auth.requestMatchers("/api/admin/users", "/api/admin/users/**", "/api/admin/sync/resync").hasRole("ADMIN");
                auth.requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "EDITOR");
                auth.requestMatchers("/api/issues", "/api/auth/me").authenticated();
                auth.requestMatchers("/internal/**").permitAll();
                auth.anyRequest().denyAll();
            });
        return http.build();
    }

    @Bean
    org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() { return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12); }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors-origins}") List<String> origins) {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(origins);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-CSRF-TOKEN", "X-Request-Id"));
        cors.setExposedHeaders(List.of("X-Request-Id", "Retry-After"));
        cors.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
