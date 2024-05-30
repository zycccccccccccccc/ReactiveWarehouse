package com.bjtu.warehousesystemwithwebflux.handler;

import com.bjtu.warehousesystemwithwebflux.pojo.Goods;
import com.bjtu.warehousesystemwithwebflux.pojo.UpdateGoodsInfoParam;
import com.bjtu.warehousesystemwithwebflux.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;


import java.util.Map;

@Component
public class GoodsHandler {

    @Autowired
    private GoodsService goodsService;

    public Mono<ServerResponse> getGoodsByName(ServerRequest request) {
        String id = request.pathVariable("id");
        return goodsService.getGoodsById(id)
                .flatMap(goods -> ServerResponse.ok().bodyValue(goods))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> addGoods(ServerRequest request) {
        return request.bodyToMono(Goods.class)
                .flatMap(goods -> goodsService.registerGoods(goods))
                .flatMap(savedGoods -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedGoods))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> outbound(ServerRequest request) {
        return request.bodyToMono(UpdateGoodsInfoParam.class)
                .flatMap(goodsInfo -> goodsService.updateGoodsOutbound(goodsInfo))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> inbound(ServerRequest request) {
        return request.bodyToMono(UpdateGoodsInfoParam.class)
                .flatMap(goodsInfo -> goodsService.updateGoodsInbound(goodsInfo))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        String id = request.pathVariable("id");
        return goodsService.deleteGoodsById(id)
                .then(noContent().build())
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> goodsList(ServerRequest request) {
        Integer pageNo = request.queryParam("pageNo").map(Integer::valueOf).orElse(1);
        Integer pageSize = request.queryParam("pageSize").map(Integer::valueOf).orElse(5);
        Integer warehouseId = request.queryParam("warehouseId").map(Integer::valueOf).orElse(null);

        return goodsService.listGoods(pageNo, pageSize, warehouseId)
                .collectList()
                .flatMap(goodsList -> ServerResponse.ok().bodyValue(goodsList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}



