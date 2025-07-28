package com.viettridao.cafe.dto.response.sales;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO trả về thông tin món ăn/thức uống cho form chọn thực đơn.
 */
@Getter
@Setter
public class MenuItemResponse {
    private Integer id;
    private String itemName;
    private Double price;
}
