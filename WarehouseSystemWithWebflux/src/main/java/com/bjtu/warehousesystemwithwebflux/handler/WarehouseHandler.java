package com.bjtu.warehousesystemwithwebflux.handler;

import com.bjtu.warehousesystemwithwebflux.pojo.Warehouse;
import com.bjtu.warehousesystemwithwebflux.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
public class WarehouseHandler {

    @Autowired
    private WarehouseService warehouseService;

    public Mono<ServerResponse> getWarehouseById(ServerRequest request) {
        String id = request.pathVariable("id");
        return warehouseService.getWarehouseById(id)
                .flatMap(warehouse -> ServerResponse.ok().bodyValue(warehouse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> addWarehouse(ServerRequest request) {
        return request.bodyToMono(Warehouse.class)
                .flatMap(warehouse -> warehouseService.addWarehouse(warehouse))
                .flatMap(warehouse -> ServerResponse.status(HttpStatus.CREATED).bodyValue(warehouse));
    }

    public Mono<ServerResponse> updateWarehouse(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Warehouse.class)
                .flatMap(warehouse -> warehouseService.updateWarehouse(id, warehouse))
                .flatMap(warehouse -> ServerResponse.ok().bodyValue(warehouse));
    }

    public Mono<ServerResponse> deleteWarehouseById(ServerRequest request) {
        String id = request.pathVariable("id");
        return warehouseService.deleteWarehouseById(id)
                .then(ServerResponse.noContent().build());
    }

}



