package com.bjtu.warehousesystemwithwebflux.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "goods")
public class Goods {

    @Id
    private String id;
    private String name;
    private Double price;
    private String category;
    private Integer warehouseId;
    private Integer quantity;
    private String description;

    public Goods(String id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }
}
