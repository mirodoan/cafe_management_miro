package com.viettridao.cafe.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.viettridao.cafe.dto.request.menu_item.CreateMenuItemRequest;
import com.viettridao.cafe.dto.request.menu_item.UpdateMenuItemRequest;
import com.viettridao.cafe.dto.request.menu_item_detail.CreateMenuItemDetailRequest;
import com.viettridao.cafe.dto.response.menu_item.MenuItemResponse;
import com.viettridao.cafe.dto.response.menu_item_detail.MenuItemDetailResponse;
import com.viettridao.cafe.model.MenuDetailEntity;
import com.viettridao.cafe.model.MenuItemEntity;
import com.viettridao.cafe.mapper.ProductMapper;

/**
 * MenuMapper
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
 * Mapper cho thực thể MenuItem và MenuDetail cho Menu Management.
 * Chuyển đổi dữ liệu giữa Entity và DTO cho quản lý thực đơn.
 */
@Component
public class MenuMapper {

    // ==== MENU ITEM MAPPING ====

    /**
     * Chuyển đổi CreateMenuItemRequest sang MenuItemEntity
     *
     * @param request request tạo mới thực đơn
     * @return MenuItemEntity
     */
    public MenuItemEntity toEntity(CreateMenuItemRequest request) {
        if (request == null)
            return null;

        MenuItemEntity entity = new MenuItemEntity();
        entity.setItemName(request.getItemName());
        entity.setCurrentPrice(request.getCurrentPrice());
        entity.setIsDeleted(false);
        return entity;
    }

    /**
     * Chuyển đổi MenuItemEntity sang MenuItemResponse (với menuDetails)
     *
     * @param entity entity thực đơn
     * @return MenuItemResponse
     */
    public MenuItemResponse toResponse(MenuItemEntity entity) {
        if (entity == null)
            return null;

        MenuItemResponse response = new MenuItemResponse();
        response.setId(entity.getId());
        response.setItemName(entity.getItemName());
        response.setCurrentPrice(entity.getCurrentPrice());
        // Map menuDetails sang response
        response.setMenuDetails(toMenuDetailResponseList(entity.getMenuDetails()));
        return response;
    }

    /**
     * Chuyển đổi danh sách MenuItemEntity sang danh sách MenuItemResponse
     *
     * @param entities danh sách entity thực đơn
     * @return danh sách MenuItemResponse
     */
    public List<MenuItemResponse> toResponseList(List<MenuItemEntity> entities) {
        if (entities == null)
            return null;
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật MenuItemEntity từ UpdateMenuItemRequest
     *
     * @param request request cập nhật thực đơn
     * @param entity entity thực đơn cần cập nhật
     */
    public void updateEntityFromRequest(UpdateMenuItemRequest request, MenuItemEntity entity) {
        if (request == null || entity == null)
            return;

        entity.setItemName(request.getItemName());
        entity.setCurrentPrice(request.getCurrentPrice());
        // menuDetails sẽ được xử lý riêng ở service layer
    }

    // ==== MENU DETAIL MAPPING ====

    /**
     * Chuyển đổi CreateMenuItemDetailRequest sang MenuDetailEntity
     *
     * @param request request tạo mới chi tiết thực đơn
     * @return MenuDetailEntity
     */
    public MenuDetailEntity toMenuDetailEntity(CreateMenuItemDetailRequest request) {
        if (request == null)
            return null;

        MenuDetailEntity entity = new MenuDetailEntity();
        entity.setQuantity(request.getQuantity());
        entity.setIsDeleted(false);
        // unitName sẽ được set từ Unit entity ở service layer
        // MenuKey, Product, MenuItem sẽ được set từ service layer
        return entity;
    }

    /**
     * Inject ProductMapper cho mapping product bên chi tiết thực đơn.
     */
    private final ProductMapper productMapper;

    public MenuMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * Chuyển đổi MenuDetailEntity sang MenuItemDetailResponse
     *
     * @param entity entity chi tiết thực đơn
     * @return MenuItemDetailResponse
     */
    public MenuItemDetailResponse toMenuDetailResponse(MenuDetailEntity entity) {
        if (entity == null)
            return null;

        MenuItemDetailResponse response = new MenuItemDetailResponse();
        response.setProduct(productMapper.toProductResponse(entity.getProduct()));
        response.setQuantity(entity.getQuantity());
        response.setUnitName(entity.getUnitName());
        // Lấy unitId từ Product -> Unit relationship
        if (entity.getProduct() != null && entity.getProduct().getUnit() != null) {
            response.setUnitId(entity.getProduct().getUnit().getId());
        }
        return response;
    }

    /**
     * Chuyển đổi danh sách MenuDetailEntity sang danh sách MenuItemDetailResponse
     *
     * @param entities danh sách entity chi tiết thực đơn
     * @return danh sách MenuItemDetailResponse
     */
    public List<MenuItemDetailResponse> toMenuDetailResponseList(List<MenuDetailEntity> entities) {
        if (entities == null)
            return null;
        return entities.stream()
                .filter(entity -> !Boolean.TRUE.equals(entity.getIsDeleted()))
                .map(this::toMenuDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi MenuItemDetailResponse sang CreateMenuItemDetailRequest (để populate form)
     *
     * @param response response chi tiết thực đơn
     * @return CreateMenuItemDetailRequest
     */
    public CreateMenuItemDetailRequest toMenuDetailRequest(MenuItemDetailResponse response) {
        if (response == null)
            return null;

        CreateMenuItemDetailRequest request = new CreateMenuItemDetailRequest();
        // Note: MenuItemDetailResponse không có ID, nên sẽ null khi populate form (sẽ được xử lý như tạo mới)
        if (response.getProduct() != null) {
            request.setProductId(response.getProduct().getId());
        }
        request.setQuantity(response.getQuantity());
        request.setUnitId(response.getUnitId());
        return request;
    }

    /**
     * Chuyển đổi danh sách MenuItemDetailResponse sang danh sách CreateMenuItemDetailRequest
     *
     * @param responses danh sách response chi tiết thực đơn
     * @return danh sách CreateMenuItemDetailRequest
     */
    public List<CreateMenuItemDetailRequest> toMenuDetailRequestList(List<MenuItemDetailResponse> responses) {
        if (responses == null)
            return null;
        return responses.stream()
                .map(this::toMenuDetailRequest)
                .collect(Collectors.toList());
    }
}