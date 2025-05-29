package com.pms.apigateway.filter;


import com.pms.apigateway.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtService jwtService;

    public AuthenticationFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Check if this is a WebSocket upgrade request
            boolean isWebSocket = exchange.getRequest().getHeaders().containsKey("Upgrade")
                    && "websocket".equalsIgnoreCase(exchange.getRequest().getHeaders().getFirst("Upgrade"));

            String token = null;

            // Try to get token from headers first
            final String tokenHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
                token = tokenHeader.split("Bearer ")[1];
            }

            // For WebSocket, also check connectionParams in the query string
            if (isWebSocket && token == null) {
                System.out.println(isWebSocket);
                token = extractTokenFromQueryParams(exchange);
            }

            if (token == null) {
                log.info("Authorization Token not found");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                String userId = jwtService.getUserIdByToken(token);
                ServerWebExchange modifiedExchange = exchange
                        .mutate()
                        .request(r -> r.header("X-User-Id", userId))
                        .build();
                return chain.filter(modifiedExchange);
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String extractTokenFromQueryParams(ServerWebExchange exchange) {
       String tokens = exchange.getRequest().getQueryParams().getFirst("Bearer");
        System.out.println(tokens);
        return tokens;
    }

    public static class Config {
    }
}