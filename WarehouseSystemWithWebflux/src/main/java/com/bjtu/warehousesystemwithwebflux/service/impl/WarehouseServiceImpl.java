package com.bjtu.warehousesystemwithwebflux.service.impl;

import com.bjtu.warehousesystemwithwebflux.pojo.Warehouse;
import com.bjtu.warehousesystemwithwebflux.repository.WarehouseRepository;
import com.bjtu.warehousesystemwithwebflux.service.WarehouseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate; // 注意这里的类型改为 String

    private static final String WAREHOUSE_CACHE_PREFIX = "warehouse:";

    @Override
    public Mono<Warehouse> getWarehouseById(String id) {
        String key = WAREHOUSE_CACHE_PREFIX + id;
        return reactiveRedisTemplate.opsForValue()
                .get(key)
                .flatMap(data -> Mono.justOrEmpty(convertToWarehouse(data))) // 将 JSON 字符串转换为 Warehouse 对象
                .switchIfEmpty(
                        warehouseRepository.findById(id)
                                .flatMap(warehouse -> reactiveRedisTemplate.opsForValue()
                                        .set(key, convertToJson(warehouse)) // 将 Warehouse 对象转换为 JSON 字符串
                                        .thenReturn(warehouse))
                );
    }

    @Override
    public Mono<Warehouse> addWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse)
                .flatMap(savedWarehouse -> reactiveRedisTemplate.opsForValue()
                        .set(WAREHOUSE_CACHE_PREFIX + savedWarehouse.getId(), convertToJson(savedWarehouse)) // 将 Warehouse 对象转换为 JSON 字符串
                        .thenReturn(savedWarehouse));
    }

    @Override
    public Mono<Warehouse> updateWarehouse(String id, Warehouse warehouse) {
        warehouse.setId(id);
        return warehouseRepository.save(warehouse)
                .flatMap(savedWarehouse -> reactiveRedisTemplate.opsForValue()
                        .set(WAREHOUSE_CACHE_PREFIX + savedWarehouse.getId(), convertToJson(savedWarehouse)) // 将 Warehouse 对象转换为 JSON 字符串
                        .thenReturn(savedWarehouse));
    }

    @Override
    public Mono<Void> deleteWarehouseById(String id) {
        return warehouseRepository.deleteById(id)
                .then(reactiveRedisTemplate.opsForValue().delete(WAREHOUSE_CACHE_PREFIX + id).then());
    }

    // 辅助方法：将 Warehouse 对象转换为 JSON 字符串
    private String convertToJson(Warehouse warehouse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(warehouse);
        } catch (JsonProcessingException e) {
            // 处理异常
            return null;
        }
    }

    // 辅助方法：将 JSON 字符串转换为 Warehouse 对象
    private Warehouse convertToWarehouse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, Warehouse.class);
        } catch (JsonProcessingException e) {
            // 处理异常
            return null;
        }
    }
}



