package com.bjtu.warehousesystemwithwebflux.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGoodsInfoParam {
    private String goodsId;
    private Integer quantity;
    private Integer warehouseId;
    private Long time;
}
