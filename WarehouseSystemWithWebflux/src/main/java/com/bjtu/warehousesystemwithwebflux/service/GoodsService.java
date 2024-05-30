package com.bjtu.warehousesystemwithwebflux.service;

import com.bjtu.warehousesystemwithwebflux.pojo.Goods;
import com.bjtu.warehousesystemwithwebflux.pojo.Inbound;
import com.bjtu.warehousesystemwithwebflux.pojo.Outbound;
import com.bjtu.warehousesystemwithwebflux.pojo.UpdateGoodsInfoParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GoodsService {

    Mono<Goods> registerGoods(Goods goods);

    Mono<Goods> getGoodsById(String id);

    Mono<Outbound> updateGoodsOutbound(UpdateGoodsInfoParam goodsInfo);

    Mono<Inbound> updateGoodsInbound(UpdateGoodsInfoParam goodsInfo);

    Mono<Void> deleteGoodsById(String id);

    Flux<Goods> listGoods(Integer pageNo, Integer pageSize, Integer warehouseId);
}

