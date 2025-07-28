package com.viettridao.cafe.dto.response.product;

/**
 * DTO cho việc biểu diễn dữ liệu liên quan đến sản phẩm.
 * Bao gồm thông tin về tên sản phẩm, số lượng, giá và đơn vị.
 */
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
    private Integer id;

    private String productName;

    private Integer quantity;

    private String unitName;

    private Boolean isDeleted;
}
