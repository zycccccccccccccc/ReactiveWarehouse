package com.bjtu.warehousesystemwithwebflux.repository;

import com.bjtu.warehousesystemwithwebflux.pojo.Outbound;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OutboundRepository extends ReactiveMongoRepository<Outbound, String> {

}

