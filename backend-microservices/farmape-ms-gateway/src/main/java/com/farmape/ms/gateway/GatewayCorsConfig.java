package com.farmape.ms.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class GatewayCorsConfig {

    private static final String DEFAULT_ALLOWED_ORIGINS =
            "http://localhost:5173,https://farmape-three.vercel.app";
    private static final String DEFAULT_ALLOWED_ORIGIN_PATTERNS = "https://*.vercel.app";
    private static final String DEFAULT_ALLOWED_METHODS = "GET,POST,PUT,PATCH,DELETE,OPTIONS";
    private static final String DEFAULT_ALLOWED_HEADERS =
            "Authorization,Content-Type,Accept,Origin,X-Requested-With";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    WebFilter gatewayCorsFilter() {
        return (exchange, chain) -> {
            String origin = exchange.getRequest().getHeaders().getOrigin();
            if (origin == null || !isAllowedOrigin(origin)) {
                return chain.filter(exchange);
            }

            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                applyCors(exchange, origin);
                exchange.getResponse().setStatusCode(HttpStatus.OK);
                return exchange.getResponse().setComplete();
            }

            exchange.getResponse().beforeCommit(() -> {
                applyCors(exchange, origin);
                return Mono.empty();
            });
            return chain.filter(exchange);
        };
    }

    private void applyCors(ServerWebExchange exchange, String origin) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        removeCorsHeaders(headers);
        headers.setAccessControlAllowOrigin(origin);
        headers.setAccessControlAllowMethods(parseMethods(env("CORS_ALLOWED_METHODS", DEFAULT_ALLOWED_METHODS)));
        headers.setAccessControlExposeHeaders(List.of(HttpHeaders.AUTHORIZATION));
        headers.setAccessControlMaxAge(3600);

        String requestedHeaders = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        headers.set(
                HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                requestedHeaders == null || requestedHeaders.isBlank()
                        ? env("CORS_ALLOWED_HEADERS", DEFAULT_ALLOWED_HEADERS)
                        : requestedHeaders
        );
        headers.setVary(List.of(
                HttpHeaders.ORIGIN,
                HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS
        ));
    }

    private void removeCorsHeaders(HttpHeaders headers) {
        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        headers.remove(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        headers.remove(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        headers.remove(HttpHeaders.VARY);
    }

    private boolean isAllowedOrigin(String origin) {
        Set<String> exactOrigins = parseCsv(env("CORS_ALLOWED_ORIGINS", DEFAULT_ALLOWED_ORIGINS));
        exactOrigins.add(env("FRONTEND_LOCAL_ORIGIN", "http://localhost:5173"));
        if (exactOrigins.contains(origin)) {
            return true;
        }
        return parseCsv(env("CORS_ALLOWED_ORIGIN_PATTERNS",
                env("FRONTEND_ALLOWED_ORIGIN_PATTERN", DEFAULT_ALLOWED_ORIGIN_PATTERNS)))
                .stream()
                .anyMatch(pattern -> matchesPattern(origin, pattern));
    }

    private boolean matchesPattern(String origin, String pattern) {
        if (!pattern.contains("*")) {
            return origin.equals(pattern);
        }
        String[] parts = pattern.split("\\*", 2);
        String prefix = parts.length > 0 ? parts[0] : "";
        String suffix = parts.length > 1 ? parts[1] : "";
        return origin.startsWith(prefix) && origin.endsWith(suffix);
    }

    private Set<String> parseCsv(String value) {
        Set<String> items = new LinkedHashSet<>();
        if (value == null || value.isBlank()) {
            return items;
        }
        Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .forEach(items::add);
        return items;
    }

    private List<HttpMethod> parseMethods(String value) {
        return parseCsv(value).stream()
                .map(HttpMethod::valueOf)
                .toList();
    }

    private String env(String name, String defaultValue) {
        return System.getenv().getOrDefault(name, defaultValue);
    }
}
