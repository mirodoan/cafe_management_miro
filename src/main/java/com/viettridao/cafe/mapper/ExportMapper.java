package com.viettridao.cafe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.viettridao.cafe.dto.request.export.CreateExportRequest;
import com.viettridao.cafe.dto.request.export.UpdateExportRequest;
import com.viettridao.cafe.dto.response.export.ExportResponse;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.ExportEntity;
import com.viettridao.cafe.model.ProductEntity;

/**
 * ExportMapper
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
 * Mapper chuyển đổi giữa ExportEntity, CreateExportRequest, UpdateExportRequest, ExportResponse sử dụng MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ExportMapper {

    /**
     * Mapping từ CreateExportRequest sang ExportEntity.
     * - id bỏ qua
     * - isDeleted luôn là false
     * - employee ánh xạ từ employeeId
     * - product ánh xạ từ productId
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "employee", source = "employeeId", qualifiedByName = "mapEmployeeId")
    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProductId")
    ExportEntity toEntity(CreateExportRequest request);

    /**
     * Mapping từ ExportEntity sang ExportResponse.
     */
    ExportResponse toResponse(ExportEntity entity);

    /**
     * Mapping từ ExportEntity sang UpdateExportRequest.
     * Có thể bật các mapping dưới nếu muốn ánh xạ id cho employee/product.
     */
//    @Mapping(target = "employeeId", source = "employee.id")
//    @Mapping(target = "productId", source = "product.id")
    UpdateExportRequest toUpdateRequest(ExportEntity entity);

    /**
     * Áp dụng các giá trị từ UpdateExportRequest vào ExportEntity (update entity).
     * - id bỏ qua
     * - isDeleted bỏ qua
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromRequest(UpdateExportRequest request, @MappingTarget ExportEntity entity);

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