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
@Document(collection = "inbound")
public class Inbound {
    @Id
    private String id;
    private String name;
    private Integer warehouseId;
    private Integer quantity;
    private Long time;
}
