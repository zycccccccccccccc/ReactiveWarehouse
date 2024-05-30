package com.bjtu.warehousesystemwithwebflux.handler;

import com.bjtu.warehousesystemwithwebflux.jwt.JwtUtil;
import com.bjtu.warehousesystemwithwebflux.pojo.LoginRequest;
import com.bjtu.warehousesystemwithwebflux.pojo.User;
import com.bjtu.warehousesystemwithwebflux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userService.registerUser(user);
                })
                .flatMap(savedUser -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedUser))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(loginRequest -> userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword()))
                .flatMap(user -> {
                    String token = jwtUtil.generateToken(user.getUsername());
                    return ServerResponse.ok().bodyValue(Map.of("token", token));
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(Map.of("error", "Invalid username or password")));
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String userId = request.pathVariable("id");
        return userService.getUserById(userId)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok().body(userService.getAllUsers(), User.class);
    }
}






