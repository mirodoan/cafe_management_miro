package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.sales.CreateSelectMenuRequest;
import com.viettridao.cafe.dto.response.sales.MenuItemResponse;
import com.viettridao.cafe.dto.response.sales.OrderDetailRessponse;

import java.util.List;

/**
 * SelectMenuService
 * Interface định nghĩa các chức năng liên quan đến chọn thực đơn, tạo order cho bàn và lấy danh sách menu.
 */
public interface SelectMenuService {
    /**
     * Tạo mới order (reservation, invoice, invoice detail) cho bàn đang trống.
     * Trả về thông tin chi tiết order sau khi tạo thành công.
     *
     * @param request    Thông tin chọn món từ frontend
     * @param employeeId ID nhân viên thao tác (lấy từ session)
     * @return OrderDetailRessponse chứa thông tin order vừa tạo
     */
    OrderDetailRessponse createOrderForAvailableTable(
            CreateSelectMenuRequest request, Integer employeeId);

    /**
     * Lấy danh sách món ăn/thức uống để hiển thị cho form chọn thực đơn.
     *
     * @return Danh sách menu item response
     */
    List<MenuItemResponse> getMenuItems();
}