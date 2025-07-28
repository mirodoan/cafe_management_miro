package com.viettridao.cafe.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.viettridao.cafe.dto.request.invoice.InvoiceItemRequest;
import com.viettridao.cafe.dto.response.invoice.InvoiceItemResponse;
import com.viettridao.cafe.model.InvoiceDetailEntity;
import com.viettridao.cafe.model.InvoiceKey;

/**
 * InvoiceDetailMapper
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
 * Mapper chuyển đổi giữa InvoiceDetailEntity, InvoiceItemRequest và InvoiceItemResponse sử dụng MapStruct.
 */
@Mapper(componentModel = "spring")
public interface InvoiceDetailMapper {

    /**
     * Mapping từ InvoiceDetailEntity sang InvoiceItemResponse.
     * - id lấy từ id.idInvoice
     * - itemName lấy từ menuItem.itemName
     * - quantity lấy trực tiếp
     * - unitPrice lấy từ price
     * - totalPrice = quantity * price
     */
    @Mappings({
            @Mapping(source = "id.idInvoice", target = "id"),
            @Mapping(source = "menuItem.itemName", target = "itemName"),
            @Mapping(source = "quantity", target = "quantity"),
            @Mapping(source = "price", target = "unitPrice"),
            @Mapping(target = "totalPrice", expression = "java(entity.getQuantity() * entity.getPrice())")
    })
    InvoiceItemResponse toDto(InvoiceDetailEntity entity);

    /**
     * Mapping từ InvoiceItemRequest sang InvoiceDetailEntity.
     * Các trường id, menuItem, price, isDeleted sẽ được xử lý ở afterMapping.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    InvoiceDetailEntity fromRequest(InvoiceItemRequest request);

    /**
     * AfterMapping: set giá trị cho id, quantity, isDeleted từ request.
     */
    @AfterMapping
    default void afterMapping(InvoiceItemRequest request, @MappingTarget InvoiceDetailEntity entity) {
        InvoiceKey key = new InvoiceKey();
        key.setIdInvoice(request.getInvoiceId());
        key.setIdMenuItem(request.getMenuItemId());
        entity.setId(key);
        entity.setQuantity(request.getQuantity());
        entity.setIsDeleted(false);
    }

    /**
     * Mapping danh sách InvoiceDetailEntity sang danh sách InvoiceItemResponse.
     */
    List<InvoiceItemResponse> toDtoList(List<InvoiceDetailEntity> entities);
}