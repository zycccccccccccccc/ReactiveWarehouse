package com.bjtu.warehousesystemwithwebflux.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "warehouse")
public class Warehouse implements Serializable {

    @Id
    private String id;
    private String name;
    private String location;


}
