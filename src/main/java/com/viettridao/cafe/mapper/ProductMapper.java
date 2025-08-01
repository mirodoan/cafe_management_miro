package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.product.ProductResponse;
import com.viettridao.cafe.model.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ProductMapper
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

        productResponse.setUnitName(
                product.getUnit() != null ? product.getUnit().getUnitName() : ""
        );
        productResponse.setQuantity(
                product.getQuantity() != null ? product.getQuantity() : 0
        );

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