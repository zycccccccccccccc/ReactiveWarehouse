package com.bjtu.warehousesystemwithwebflux;

import com.bjtu.warehousesystemwithwebflux.handler.UserHandler;
import com.bjtu.warehousesystemwithwebflux.jwt.JwtAuthenticationManager;
import com.bjtu.warehousesystemwithwebflux.jwt.JwtSecurityContextRepository;
import com.bjtu.warehousesystemwithwebflux.jwt.SecurityConfig;
import com.bjtu.warehousesystemwithwebflux.pojo.LoginRequest;
import com.bjtu.warehousesystemwithwebflux.router.UserRouter;
import com.bjtu.warehousesystemwithwebflux.service.UserService;
import com.bjtu.warehousesystemwithwebflux.pojo.User;
import com.bjtu.warehousesystemwithwebflux.jwt.JwtUtil;
import com.bjtu.warehousesystemwithwebflux.repository.UserRepository;
import com.bjtu.warehousesystemwithwebflux.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebFluxTest
@Import({UserRouter.class, UserHandler.class, UserServiceImpl.class, JwtUtil.class, SecurityConfig.class})
public class UserRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationManager jwtAuthenticationManager;

    @MockBean
    private JwtSecurityContextRepository jwtSecurityContextRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, passwordEncoder, jwtUtil, jwtAuthenticationManager, jwtSecurityContextRepository);

        // Mock JwtSecurityContextRepository to return an empty SecurityContext
        Mockito.when(jwtSecurityContextRepository.load(Mockito.any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    public void testRegister() {
        User newUser = new User("123", "username", "password");
        User savedUser = new User("123", "username", "encodedPassword");

        Mockito.when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userService.registerUser(Mockito.any(User.class))).thenReturn(Mono.just(savedUser));

        webTestClient.post().uri("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .consumeWith(response -> {
                    User responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert "encodedPassword".equals(responseBody.getPassword());
                });
    }


    @Test
    public void testLogin() {
        String username = "username";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String token = "mock-jwt-token";
        User user = new User("1", username, encodedPassword);

        Mockito.when(userService.loginUser(username, password)).thenReturn(Mono.just(user));
        Mockito.when(jwtUtil.generateToken(username)).thenReturn(token);
        Mockito.when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        LoginRequest loginRequest = new LoginRequest(username, password);

        webTestClient.post().uri("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert token.equals(responseBody.get("token"));
                });
    }

    @Test
    public void testGetUserByIdWithJWT() {
        String userId = "1";
        User user = new User(userId, "zyc", "123456");

        // Mock the methods
        Mockito.when(userService.getUserById(userId)).thenReturn(Mono.just(user));
        Mockito.when(userService.loginUser(user.getUsername(), user.getPassword())).thenReturn(Mono.just(user));

        // 登录以获取JWT令牌
        LoginRequest loginRequest = new LoginRequest("zyc", "123456");
        String token = "mock-jwt-token";

        Mockito.when(jwtUtil.generateToken(user.getUsername())).thenReturn(token);
        Mockito.when(jwtUtil.validateToken(token, user.getUsername())).thenReturn(true);

        Object tokenObj = webTestClient.post().uri("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult().getResponseBody()
                .get("token");

        String jwt = tokenObj != null ? tokenObj.toString() : null;

        if (jwt == null) {
            throw new IllegalStateException("Failed to obtain JWT token from login response");
        }

        // 使用JWT访问受保护的路由
        webTestClient.get().uri("/users/" + userId)
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith(response -> {
                    User responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert userId.equals(responseBody.getId());
                });
    }


    @Test
    public void testGetAllUsersWithJWT() {
        List<User> users = List.of(new User("1", "username1", "password1"), new User("2", "username2", "password2"));
        String token = "mock-jwt-token";

        Mockito.when(userService.getAllUsers()).thenReturn(Flux.fromIterable(users));
        Mockito.when(jwtUtil.generateToken("username")).thenReturn(token);

        // First, login to get a JWT
        LoginRequest loginRequest = new LoginRequest("username", "password");
        User loginUser = new User("1", "username", "encodedPassword");

        Mockito.when(userService.loginUser("username", "password")).thenReturn(Mono.just(loginUser));
        Mockito.when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        Mockito.when(jwtUtil.generateToken(loginUser.getUsername())).thenReturn(token);
        Mockito.when(jwtUtil.validateToken(token, loginUser.getUsername())).thenReturn(true);

        Object tokenObj = webTestClient.post().uri("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult().getResponseBody()
                .get("token");

        String jwt = tokenObj != null ? tokenObj.toString() : null;

        if (jwt == null) {
            throw new IllegalStateException("Failed to obtain JWT token from login response");
        }

        // Now use the JWT to access protected route
        webTestClient.get().uri("/users")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .consumeWith(response -> {
                    List<User> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.size() == users.size();
                });
    }

}



