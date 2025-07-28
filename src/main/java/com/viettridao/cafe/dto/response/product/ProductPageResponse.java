package com.viettridao.cafe.dto.response.product;

import java.util.List;

import com.viettridao.cafe.dto.response.PageResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho phản hồi phân trang dữ liệu sản phẩm hàng hóa.
 * Kế thừa lớp PageResponse tổng quát.
 */
@Getter
@Setter
public class ProductPageResponse extends PageResponse {
    private List<ProductResponse> products;
}