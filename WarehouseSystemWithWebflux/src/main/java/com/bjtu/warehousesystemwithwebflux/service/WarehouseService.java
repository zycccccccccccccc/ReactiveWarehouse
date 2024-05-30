package com.bjtu.warehousesystemwithwebflux.service;

import com.bjtu.warehousesystemwithwebflux.pojo.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WarehouseService {

    Mono<Warehouse> getWarehouseById(String id);

    Mono<Warehouse> addWarehouse(Warehouse warehouse);

    Mono<Warehouse> updateWarehouse(String id, Warehouse warehouse);

    Mono<Void> deleteWarehouseById(String id);
}

