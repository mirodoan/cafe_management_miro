package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.product.ProductResponse;
import com.viettridao.cafe.model.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ProductMapper
 *
 * Version 1.0
 *
 * Date: 19-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 19-07-2025   mirodoan    Create
 *
 * Mapper cho thực thể Product và DTO ProductResponse.
 * Chuyển đổi dữ liệu giữa ProductEntity và ProductResponse.
 */
@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ModelMapper modelMapper;

    /**
     * Chuyển đổi từ ProductEntity sang ProductResponse.
     *
     * @param product Thực thể ProductEntity cần chuyển đổi.
     * @return Đối tượng ProductResponse tương ứng.
     */
    public ProductResponse toProductResponse(ProductEntity product) {
        ProductResponse productResponse = new ProductResponse();
        modelMapper.map(product, productResponse);

        if (product.getUnit() != null) {
            productResponse.setUnitName(product.getUnit().getUnitName());
        }
        return productResponse;
    }

    /**
     * Chuyển đổi danh sách ProductEntity sang danh sách ProductResponse.
     *
     * @param products Danh sách thực thể ProductEntity cần chuyển đổi.
     * @return Danh sách đối tượng ProductResponse tương ứng.
     */
    public List<ProductResponse> toListProductResponse(List<ProductEntity> products) {
        return products.stream().map(this::toProductResponse).toList();
    }
}