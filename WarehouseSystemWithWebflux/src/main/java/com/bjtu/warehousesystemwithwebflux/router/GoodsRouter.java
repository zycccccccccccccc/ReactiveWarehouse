package com.bjtu.warehousesystemwithwebflux.router;

import com.bjtu.warehousesystemwithwebflux.handler.GoodsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class GoodsRouter {

    @Bean
    public RouterFunction<ServerResponse> goodsRoutes(GoodsHandler goodsHandler) {
        return RouterFunctions.route()
                .GET("/goods/{id}", goodsHandler::getGoodsByName)
                .POST("/goods", accept(MediaType.APPLICATION_JSON), goodsHandler::addGoods)
                .PUT("/goods/outbound", accept(MediaType.APPLICATION_JSON), goodsHandler::outbound)
                .PUT("/goods/inbound", accept(MediaType.APPLICATION_JSON), goodsHandler::inbound)
                .POST("/goods/{id}", goodsHandler::deleteById)
                .GET("/goods", RequestPredicates.queryParam("pageNo", p -> true)
                .and(RequestPredicates.queryParam("pageSize", p -> true))
                .and(RequestPredicates.queryParam("warehouseId", p -> true)), goodsHandler::goodsList)
                .build();
    }
}

