package com.bjtu.warehousesystemwithwebflux.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RateLimiterFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final RateLimiter rateLimiter;

    public RateLimiterFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return Mono.just(request)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .flatMap(next::handle)
                .onErrorResume(throwable -> ServerResponse.status(429).build());
    }
}

