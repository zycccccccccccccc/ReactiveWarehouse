package com.bjtu.warehousesystemwithwebflux.repository;

import com.bjtu.warehousesystemwithwebflux.pojo.Inbound;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface InboundRepository extends ReactiveMongoRepository<Inbound, String> {

}

