package com.bjtu.warehousesystemwithwebflux.service;

import com.bjtu.warehousesystemwithwebflux.pojo.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> registerUser(User user);
    Mono<User> loginUser(String username, String password);
    Mono<User> getUserById(String id);
    Flux<User> getAllUsers();
}

