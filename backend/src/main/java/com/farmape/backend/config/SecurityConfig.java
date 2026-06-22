package com.farmape.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import org.springframework.http.HttpMethod;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers("/api/usuarios/**", "/api/trabajadores/**")
                        .hasAuthority("USER_MANAGE")
                        .requestMatchers(HttpMethod.GET, "/api/roles/**")
                        .hasAuthority("ROLE_READ")
                        .requestMatchers(HttpMethod.GET, "/api/permisos/**")
                        .hasAuthority("ROLE_READ")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/*/permisos")
                        .hasAuthority("ROLE_ASSIGN")
                        .requestMatchers(HttpMethod.POST, "/api/roles/**")
                        .hasAuthority("ROLE_MANAGE")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/**")
                        .hasAuthority("ROLE_MANAGE")
                        .requestMatchers(HttpMethod.PATCH, "/api/roles/**")
                        .hasAuthority("ROLE_MANAGE")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/**")
                        .hasAuthority("ROLE_MANAGE")
                        .requestMatchers(HttpMethod.GET, "/api/productos/**")
                        .hasAnyAuthority("PRODUCT_READ", "PRODUCT_MANAGE")
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**")
                        .hasAnyAuthority("PRODUCT_READ", "PRODUCT_MANAGE")
                        .requestMatchers("/api/productos/**")
                        .hasAuthority("PRODUCT_MANAGE")
                        .requestMatchers("/api/clientes/**")
                        .hasAuthority("CUSTOMER_MANAGE")
                        .requestMatchers(HttpMethod.GET, "/api/ventas/**")
                        .hasAnyAuthority("SALE_READ", "SALE_CREATE", "PAYMENT_READ")
                        .requestMatchers(HttpMethod.POST, "/api/ventas/**")
                        .hasAuthority("SALE_CREATE")
                        .requestMatchers(HttpMethod.PATCH, "/api/ventas/*/confirmar")
                        .hasAuthority("SALE_CONFIRM")
                        .requestMatchers(HttpMethod.PATCH, "/api/ventas/*/rechazar")
                        .hasAuthority("SALE_CANCEL")
                        .requestMatchers(HttpMethod.PATCH, "/api/ventas/*/anular")
                        .hasAuthority("SALE_CANCEL")
                        .requestMatchers(HttpMethod.GET, "/api/caja/**")
                        .hasAuthority("PAYMENT_READ")
                        .requestMatchers(HttpMethod.POST, "/api/caja/**")
                        .hasAuthority("PAYMENT_CREATE")
                        .requestMatchers("/api/reportes/**")
                        .hasAuthority("REPORT_VIEW")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            List<String> permisos = jwt.getClaimAsStringList("permisos");
            Collection<GrantedAuthority> authorities = permisos == null
                    ? List.of()
                    : permisos.stream()
                            .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                            .toList();

            return new JwtAuthenticationToken(
                    jwt,
                    authorities,
                    jwt.getSubject()
            );
        };
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
