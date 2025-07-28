package com.viettridao.cafe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.viettridao.cafe.dto.request.imports.CreateImportRequest;
import com.viettridao.cafe.dto.request.imports.UpdateImportRequest;
import com.viettridao.cafe.dto.response.imports.ImportResponse;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.ImportEntity;
import com.viettridao.cafe.model.ProductEntity;

/**
 * ImportMapper
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
 * Mapper chuyển đổi giữa ImportEntity, CreateImportRequest, UpdateImportRequest, ImportResponse sử dụng MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ImportMapper {

    /**
     * Mapping từ CreateImportRequest sang ImportEntity.
     * - id bỏ qua
     * - isDeleted luôn là false
     * - employee ánh xạ từ employeeId
     * - product ánh xạ từ productId
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "employee", source = "employeeId", qualifiedByName = "mapEmployeeId")
    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProductId")
    ImportEntity toEntity(CreateImportRequest request);

    /**
     * Mapping từ ImportEntity sang ImportResponse.
     */
    ImportResponse toResponse(ImportEntity entity);

    /**
     * Mapping từ ImportEntity sang UpdateImportRequest.
     * Có thể bật các mapping dưới nếu muốn ánh xạ id cho employee/product.
     */
//    @Mapping(target = "employeeId", source = "employee.id")
//    @Mapping(target = "productId", source = "product.id")
    UpdateImportRequest toUpdateRequest(ImportEntity entity);

    /**
     * Áp dụng các giá trị từ UpdateImportRequest vào ImportEntity (update entity).
     * - id bỏ qua
     * - isDeleted bỏ qua
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromRequest(UpdateImportRequest request, @MappingTarget ImportEntity entity);

    /**
     * Hàm hỗ trợ: ánh xạ employeeId sang EmployeeEntity.
     */
    @Named("mapEmployeeId")
    default EmployeeEntity mapEmployeeId(Integer employeeId) {
        if (employeeId == null)
            return null;
        EmployeeEntity employee = new EmployeeEntity();
        employee.setId(employeeId);
        return employee;
    }

    /**
     * Hàm hỗ trợ: ánh xạ productId sang ProductEntity.
     */
    @Named("mapProductId")
    default ProductEntity mapProductId(Integer productId) {
        if (productId == null)
            return null;
        ProductEntity product = new ProductEntity();
        product.setId(productId);
        return product;
    }

}