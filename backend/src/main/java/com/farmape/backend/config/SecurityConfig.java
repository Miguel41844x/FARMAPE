package com.farmape.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/solicitar-restablecimiento",
                                "/api/auth/refresh"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/auth/solicitudes-restablecimiento")
                        .hasAuthority("USER_MANAGE")

                        .requestMatchers("/api/perfil/**")
                        .authenticated()

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                        .hasAuthority("AUDIT_MANAGE")

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
                        .hasAnyAuthority("PRODUCT_READ", "PRODUCT_MANAGE", "FORMULA_MANAGE")
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**")
                        .hasAnyAuthority("PRODUCT_READ", "PRODUCT_MANAGE")
                        .requestMatchers("/api/productos/**")
                        .hasAuthority("PRODUCT_MANAGE")

                        .requestMatchers("/api/clientes/**")
                        .hasAnyAuthority("CUSTOMER_MANAGE", "FORMULA_MANAGE", "SALE_CREATE")

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

                        .requestMatchers(HttpMethod.GET, "/api/despacho/**")
                        .hasAnyAuthority("DISPATCH_MANAGE", "INVENTORY_MANAGE")
                        .requestMatchers("/api/despacho/**")
                        .hasAuthority("DISPATCH_MANAGE")

                        .requestMatchers(HttpMethod.GET, "/api/almacen/**")
                        .hasAnyAuthority("INVENTORY_MANAGE", "DISPATCH_MANAGE")
                        .requestMatchers("/api/almacen/**")
                        .hasAuthority("INVENTORY_MANAGE")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/proveedores/**",
                                "/api/ordenes-compra/**",
                                "/api/facturas-proveedor/**",
                                "/api/notas-credito-proveedor/**",
                                "/api/pagos-proveedor/**"
                        )
                        .hasAuthority("PURCHASE_MANAGE")

                        .requestMatchers(
                                "/api/proveedores/**",
                                "/api/ordenes-compra/**",
                                "/api/facturas-proveedor/**",
                                "/api/notas-credito-proveedor/**",
                                "/api/pagos-proveedor/**"
                        )
                        .hasAuthority("PURCHASE_MANAGE")

                        .requestMatchers("/api/formulas/**")
                        .hasAuthority("FORMULA_MANAGE")

                        .requestMatchers(HttpMethod.GET, "/api/auditoria/**")
                        .hasAuthority("AUDIT_VIEW")
                        .requestMatchers(HttpMethod.POST, "/api/auditoria/**")
                        .hasAuthority("AUDIT_MANAGE")

                        .requestMatchers(HttpMethod.GET, "/api/reportes/**")
                        .hasAuthority("REPORT_VIEW")
                        .requestMatchers("/api/reportes/**")
                        .hasAuthority("REPORT_MANAGE")

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
                            .map(SimpleGrantedAuthority::new)
                            .map(GrantedAuthority.class::cast)
                            .toList();

            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        };
    }

    @Bean
    AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
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

        String allowedOrigins = System.getenv().getOrDefault(
                "CORS_ALLOWED_ORIGINS",
                "https://farmape-three.vercel.app,http://localhost:5173"
        );

        String allowedOriginPatterns = System.getenv().getOrDefault(
                "CORS_ALLOWED_ORIGIN_PATTERNS",
                "https://*.vercel.app"
        );

        config.setAllowedOrigins(parseCsv(allowedOrigins));
        config.setAllowedOriginPatterns(parseCsv(allowedOriginPatterns));

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        config.setExposedHeaders(List.of(
                "Authorization"
        ));

        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    private List<String> parseCsv(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .distinct()
                .toList();
    }
}
