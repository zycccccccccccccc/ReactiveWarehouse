package com.bjtu.warehousesystemwithwebflux.repository;

import com.bjtu.warehousesystemwithwebflux.pojo.Goods;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface GoodsRepository extends ReactiveMongoRepository<Goods, String> {
    @Query("{ 'warehouseId' : ?2 }")
    Flux<Goods> findGoodsByPageAndWarehouseId(Integer pageNo, Integer pageSize, Integer warehouseId);
}

