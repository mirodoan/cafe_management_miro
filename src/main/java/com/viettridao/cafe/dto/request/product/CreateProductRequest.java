package com.viettridao.cafe.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho việc tạo mới thông tin sản phẩm.
 * Bao gồm các trường như tên sản phẩm, giá, và đơn vị tính.
 */
@Getter
@Setter
public class CreateProductRequest {
    @NotBlank(message = "Tên hàng hóa không được để trống")
    @Size(min = 3, message = "Tên hàng hóa tối thiểu 3 ký tự")
    private String productName;

    @NotNull(message = "Id đơn vị tính không được để trống")
    @Min(value = 1, message = "Id đơn vị tính phải lớn hơn 0")
    private Integer unitId;
}
