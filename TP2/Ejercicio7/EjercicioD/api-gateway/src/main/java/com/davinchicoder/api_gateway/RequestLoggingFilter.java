package com.davinchicoder.api_gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = extractUsername(authentication);

                    log.info("Incoming request method={} path={} query={} user={}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath(),
                            exchange.getRequest().getURI().getQuery(),
                            username
                    );

                    return chain.filter(exchange)
                            .doFinally(signalType -> {
                                int status = exchange.getResponse().getStatusCode() != null
                                        ? exchange.getResponse().getStatusCode().value()
                                        : 0;

                                long durationMs = System.currentTimeMillis() - start;

                                log.info("Outgoing response method={} path={} status={} durationMs={} user={}",
                                        exchange.getRequest().getMethod(),
                                        exchange.getRequest().getPath(),
                                        status,
                                        durationMs,
                                        username
                                );
                            });
                });
    }

    private String extractUsername(Authentication authentication) {
        return authentication != null ? authentication.getName() : "anonymous";
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
