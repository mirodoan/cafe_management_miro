package com.viettridao.cafe.dto.response.menu_item_detail;

import com.viettridao.cafe.dto.response.product.ProductResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemDetailResponse {
    private ProductResponse product;
    private Double quantity;
    private Integer unitId;
    private String unitName;
}