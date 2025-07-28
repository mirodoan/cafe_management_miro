package com.viettridao.cafe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.viettridao.cafe.dto.response.warehouse_transaction.WarehouseTransactionResponse;
import com.viettridao.cafe.model.ExportEntity;
import com.viettridao.cafe.model.ImportEntity;

/**
 * WarehouseTransactionMapper
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
 * Mapper chuyển đổi giữa ImportEntity/ExportEntity và WarehouseTransactionResponse.
 */
@Mapper(componentModel = "spring")
public interface WarehouseTransactionMapper {

    /**
     * Mapping từ ImportEntity sang WarehouseTransactionResponse (phiếu nhập kho).
     * - productName: tên sản phẩm
     * - unitName: tên đơn vị
     * - quantity: số lượng nhập
     * - unitPrice: giá nhập
     * - totalAmount: tổng tiền nhập
     * - importDate: ngày nhập
     * - type: IMPORT
     * - exportDate: bỏ qua
     */
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.unit.unitName", target = "unitName")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitImportPrice", target = "unitPrice")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "importDate", target = "importDate")
    @Mapping(target = "exportDate", ignore = true)
    @Mapping(target = "type", constant = "IMPORT")
    WarehouseTransactionResponse fromImport(ImportEntity importEntity);

    /**
     * Mapping từ ExportEntity sang WarehouseTransactionResponse (phiếu xuất kho).
     * - productName: tên sản phẩm
     * - unitName: tên đơn vị
     * - quantity: số lượng xuất
     * - unitPrice: giá xuất
     * - totalAmount: tổng tiền xuất
     * - exportDate: ngày xuất
     * - type: EXPORT
     * - importDate: bỏ qua
     */
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.unit.unitName", target = "unitName")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitExportPrice", target = "unitPrice")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "exportDate", target = "exportDate")
    @Mapping(target = "importDate", ignore = true)
    @Mapping(target = "type", constant = "EXPORT")
    WarehouseTransactionResponse fromExport(ExportEntity exportEntity);
}