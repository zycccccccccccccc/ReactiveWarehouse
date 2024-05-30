package com.bjtu.warehousesystemwithwebflux.repository;

import com.bjtu.warehousesystemwithwebflux.pojo.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByUsername(String username);
}

