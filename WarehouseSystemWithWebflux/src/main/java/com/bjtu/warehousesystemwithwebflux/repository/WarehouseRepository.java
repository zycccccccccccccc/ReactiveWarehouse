package com.bjtu.warehousesystemwithwebflux.repository;

import com.bjtu.warehousesystemwithwebflux.pojo.Warehouse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface WarehouseRepository extends ReactiveMongoRepository<Warehouse, String> {
}

