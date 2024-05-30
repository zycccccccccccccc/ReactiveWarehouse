package com.bjtu.warehousesystemwithwebflux;

import com.bjtu.warehousesystemwithwebflux.handler.GoodsHandler;
import com.bjtu.warehousesystemwithwebflux.jwt.JwtAuthenticationManager;
import com.bjtu.warehousesystemwithwebflux.jwt.JwtSecurityContextRepository;
import com.bjtu.warehousesystemwithwebflux.jwt.JwtUtil;
import com.bjtu.warehousesystemwithwebflux.jwt.SecurityConfig;
import com.bjtu.warehousesystemwithwebflux.pojo.*;
import com.bjtu.warehousesystemwithwebflux.router.GoodsRouter;
import com.bjtu.warehousesystemwithwebflux.service.GoodsService;
import com.bjtu.warehousesystemwithwebflux.service.UserService;
import com.bjtu.warehousesystemwithwebflux.service.impl.GoodsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@WebFluxTest
@Import({GoodsRouter.class, GoodsHandler.class, GoodsServiceImpl.class, JwtUtil.class, SecurityConfig.class})
public class GoodsRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GoodsService goodsService;

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
        Mockito.reset(goodsService, userService, passwordEncoder, jwtUtil, jwtAuthenticationManager, jwtSecurityContextRepository);

        // Mock JwtSecurityContextRepository to return an empty SecurityContext
        Mockito.when(jwtSecurityContextRepository.load(Mockito.any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    public void testAddGoods() {
        Goods newGoods = new Goods("1", "goodsName", 100);
        Goods savedGoods = new Goods("1", "goodsName", 100);

        Mockito.when(goodsService.registerGoods(Mockito.any(Goods.class))).thenReturn(Mono.just(savedGoods));

        webTestClient.post().uri("/goods")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newGoods)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Goods.class)
                .consumeWith(response -> {
                    Goods responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert "goodsName".equals(responseBody.getName());
                });
    }

    @Test
    public void testGetGoodsByIdWithJWT() {
        String goodsId = "1";
        Goods goods = new Goods(goodsId, "goodsName", 100);

        // Mock the methods
        Mockito.when(goodsService.getGoodsById(goodsId)).thenReturn(Mono.just(goods));

        // Login to get JWT token
        String username = "username";
        String password = "password";
        User user = new User("1", username, "encodedPassword");
        String token = "mock-jwt-token";

        Mockito.when(jwtUtil.generateToken(username)).thenReturn(token);
        Mockito.when(jwtUtil.validateToken(token, username)).thenReturn(true);
        Mockito.when(userService.loginUser(username, password)).thenReturn(Mono.just(user));
        Mockito.when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        LoginRequest loginRequest = new LoginRequest(username, password);
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

        // Use JWT to access protected route
        webTestClient.get().uri("/goods/" + goodsId)
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Goods.class)
                .consumeWith(response -> {
                    Goods responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert goodsId.equals(responseBody.getId());
                });
    }

    @Test
    public void testGetAllGoodsWithJWT() {
        List<Goods> goodsList = List.of(new Goods("1", "goodsName1", 100), new Goods("2", "goodsName2", 200));
        String token = "mock-jwt-token";

        Mockito.when(goodsService.listGoods(1, 10, 123)).thenReturn(Flux.fromIterable(goodsList));

        // Login to get JWT token
        String username = "username";
        String password = "password";
        User user = new User("1", username, "encodedPassword");
        Mockito.when(userService.loginUser(username, password)).thenReturn(Mono.just(user));
        Mockito.when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken(username)).thenReturn(token);
        Mockito.when(jwtUtil.validateToken(token, username)).thenReturn(true);

        LoginRequest loginRequest = new LoginRequest(username, password);
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

        // Use JWT to access protected route
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/goods")
                        .queryParam("pageNo", 1)
                        .queryParam("pageSize", 10)
                        .queryParam("warehouseId", 123)
                        .build())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Goods.class)
                .consumeWith(response -> {
                    List<Goods> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.size() == goodsList.size();
                });
    }




    @Test
    public void testDeleteGoodsById() {
        String goodsId = "1";

        Mockito.when(goodsService.deleteGoodsById(goodsId)).thenReturn(Mono.empty());

        webTestClient.post().uri("/goods/" + goodsId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}

