package com.wdreslerski.product.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/products/**",
                                                                "/api/categories/**",
                                                                "/actuator/**")
                                                .permitAll() // We permit all here because method security handles the
                                                             // rest? Or should we
                                                             // secure everything?
                                                // Actually, GET requests are public usually. But method security
                                                // @PreAuthorize("hasRole('ADMIN')") is used on mutations.
                                                // Let's rely on method security but authenticate everyone who has a
                                                // token.
                                                .anyRequest().authenticated()) // Wait, if I permitAll, the filter still
                                                                               // runs? Yes. Filter runs
                                                                               // before.
                                // However, if I use permitAll, anonymous users can access.
                                // The monolith had permitAll for GETs? Let's check ProductController logic in
                                // monolith.
                                // It only had @PreAuthorize on POST/PUT/DELETE. GET was open.
                                // So permitAll() is correct for GETs if method security is not present.
                                // BUT, to be safe, let's allow GETs explicitly and authenticate others.
                                // Better yet: permitAll() for everything, rely on @PreAuthorize validation for
                                // write operations.
                                // AND rely on filter to populate context if token exists. If no token, context
                                // is anonymous.

                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
