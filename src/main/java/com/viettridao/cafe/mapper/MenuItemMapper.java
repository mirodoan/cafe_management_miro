package com.viettridao.cafe.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.viettridao.cafe.dto.response.sales.MenuItemResponse;
import com.viettridao.cafe.model.MenuItemEntity;

/**
 * MenuItemMapper
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
 * Mapper cho Sales - chuyển đổi MenuItemEntity sang MenuItemResponse cho nghiệp vụ bán hàng/chọn món.
 * Chỉ mapping các trường cơ bản: id, itemName, price.
 */
public class MenuItemMapper {
    /**
     * Chuyển đổi MenuItemEntity sang MenuItemResponse cho Sales (chỉ cần id, tên, giá).
     *
     * @param entity MenuItemEntity nguồn
     * @return MenuItemResponse
     */
    public static MenuItemResponse toMenuItemResponse(MenuItemEntity entity) {
        if (entity == null)
            return null;
        MenuItemResponse dto = new MenuItemResponse();
        dto.setId(entity.getId());
        dto.setItemName(entity.getItemName());
        dto.setPrice(entity.getCurrentPrice());
        return dto;
    }

    /**
     * Chuyển đổi danh sách MenuItemEntity sang danh sách MenuItemResponse cho Sales.
     *
     * @param entities danh sách MenuItemEntity
     * @return danh sách MenuItemResponse
     */
    public static List<MenuItemResponse> toMenuItemResponseList(List<MenuItemEntity> entities) {
        if (entities == null)
            return null;
        return entities.stream().map(MenuItemMapper::toMenuItemResponse).collect(Collectors.toList());
    }
}