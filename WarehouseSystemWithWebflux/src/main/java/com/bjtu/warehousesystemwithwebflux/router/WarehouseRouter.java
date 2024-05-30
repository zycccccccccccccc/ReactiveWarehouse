package com.bjtu.warehousesystemwithwebflux.router;

import com.bjtu.warehousesystemwithwebflux.filter.RateLimiterFilter;
import com.bjtu.warehousesystemwithwebflux.handler.GoodsHandler;
import com.bjtu.warehousesystemwithwebflux.handler.WarehouseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class WarehouseRouter {

    @Bean
    public RouterFunction<ServerResponse> warehouseRoutes(WarehouseHandler warehouseHandler, RateLimiterFilter rateLimiterFilter) {
        return RouterFunctions.route()
                .GET("/warehouse/{id}", warehouseHandler::getWarehouseById)
                .POST("/warehouse", accept(MediaType.APPLICATION_JSON), warehouseHandler::addWarehouse)
                .PUT("/warehouse/{id}", accept(MediaType.APPLICATION_JSON), warehouseHandler::updateWarehouse)
                .DELETE("/warehouse/{id}", warehouseHandler::deleteWarehouseById)
                .filter(rateLimiterFilter)
                .build();
    }
}


