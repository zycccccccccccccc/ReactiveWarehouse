package com.bjtu.warehousesystemwithwebflux.router;

import com.bjtu.warehousesystemwithwebflux.handler.UserHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class UserRouter {

    @Autowired
    private UserHandler userHandler;

    @Bean
    public RouterFunction<ServerResponse> userRoutes() {
        return RouterFunctions.route()
                .POST("/users/register", accept(MediaType.APPLICATION_JSON), userHandler::register)
                .POST("/users/login", accept(MediaType.APPLICATION_JSON), userHandler::login)
                .GET("/users/{id}", userHandler::getUserById)
                .GET("/users", userHandler::getAllUsers)
                .build();
    }
}
