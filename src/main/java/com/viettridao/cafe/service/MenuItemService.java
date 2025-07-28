package com.viettridao.cafe.service;

import java.util.List;

import com.viettridao.cafe.dto.request.menu_item.CreateMenuItemRequest;
import com.viettridao.cafe.dto.request.menu_item.UpdateMenuItemRequest;
import com.viettridao.cafe.dto.response.menu_item.MenuItemPageResponse;
import com.viettridao.cafe.dto.response.menu_item.MenuItemResponse;
import com.viettridao.cafe.model.MenuItemEntity;

/**
 * MenuItemService
 *
 * Version 1.0
 *
 * Date: 18-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 18-07-2025   mirodoan    Create
 */
public interface MenuItemService {

    /**
     * Lấy danh sách tất cả món theo từ khóa tìm kiếm và phân trang.
     *
     * @param keyword Từ khóa tìm kiếm theo tên món.
     * @param page    Số trang cần lấy.
     * @param size    Số lượng bản ghi trên mỗi trang.
     * @return Đối tượng MenuItemPageResponse chứa danh sách món và thông tin phân trang.
     */
    MenuItemPageResponse getAllMenuItems(String keyword, int page, int size);

    /**
     * Tạo mới một món.
     *
     * @param request Đối tượng chứa thông tin cần thiết để tạo món mới.
     * @return Thực thể MenuItemEntity vừa được tạo.
     */
    MenuItemEntity createMenuItem(CreateMenuItemRequest request);

    /**
     * Cập nhật thông tin món.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho món.
     */
    void updateMenuItem(UpdateMenuItemRequest request);

    /**
     * Xóa một món dựa trên ID.
     *
     * @param id ID của món cần xóa.
     */
    void deleteMenuItem(Integer id);

    /**
     * Lấy thông tin chi tiết của một món dựa trên ID.
     *
     * @param id ID của món cần lấy thông tin.
     * @return Thực thể MenuItemEntity tương ứng với ID.
     */
    MenuItemEntity getMenuItemById(Integer id);

    /**
     * Kiểm tra tên món đã tồn tại trong hệ thống chưa (dành cho tạo mới món).
     * @param itemName tên món
     * @return true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByItemName(String itemName);

    /**
     * Kiểm tra tên món đã tồn tại trong hệ thống chưa (dành cho cập nhật món).
     * @param itemName tên món
     * @param menuItemId ID món cần loại trừ khỏi kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByItemNameAndIdNot(String itemName, Integer menuItemId);
}