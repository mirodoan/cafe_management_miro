package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.dto.request.menu_item.CreateMenuItemRequest;
import com.viettridao.cafe.dto.request.menu_item.UpdateMenuItemRequest;
import com.viettridao.cafe.dto.request.menu_item_detail.CreateMenuItemDetailRequest;
import com.viettridao.cafe.dto.response.menu_item.MenuItemPageResponse;
import com.viettridao.cafe.mapper.MenuMapper;
import com.viettridao.cafe.model.*;
import com.viettridao.cafe.repository.MenuItemDetailRepository;
import com.viettridao.cafe.repository.MenuItemRepository;
import com.viettridao.cafe.repository.ProductRepository;
import com.viettridao.cafe.service.MenuItemService;
import com.viettridao.cafe.service.UnitService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MenuItemServiceImpl
 * Triển khai Service cho thực đơn (MenuItem).
 * Xử lý lấy danh sách, tạo mới, cập nhật, xóa mềm, kiểm tra tên món, thêm chi tiết món.
 */
@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemDetailRepository menuItemDetailRepository;
    private final MenuMapper menuMapper;
    private final ProductRepository productRepository;
    private final UnitService unitService;

    /**
     * Lấy danh sách thực đơn (MenuItem) theo keyword và phân trang.
     *
     * @param keyword từ khóa tìm kiếm theo tên món.
     * @param page    số trang.
     * @param size    kích thước trang.
     * @return MenuItemPageResponse danh sách thực đơn.
     */
    @Override
    public MenuItemPageResponse getAllMenuItems(String keyword, int page, int size) {
        Page<MenuItemEntity> menuItemPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            menuItemPage = menuItemRepository.getAllByMenuItems(keyword, PageRequest.of(page, size));
        } else {
            menuItemPage = menuItemRepository.getAllByMenuItems(PageRequest.of(page, size));
        }

        MenuItemPageResponse response = new MenuItemPageResponse();
        response.setPageNumber(menuItemPage.getNumber());
        response.setPageSize(menuItemPage.getSize());
        response.setTotalElements(menuItemPage.getTotalElements());
        response.setTotalPages(menuItemPage.getTotalPages());
        response.setMenuItems(menuMapper.toResponseList(menuItemPage.getContent()));

        return response;
    }

    /**
     * Tạo mới một thực đơn và các chi tiết món liên kết.
     *
     * @param request thông tin tạo thực đơn mới.
     * @return MenuItemEntity vừa được tạo.
     */
    @Transactional
    @Override
    public MenuItemEntity createMenuItem(CreateMenuItemRequest request) {
        MenuItemEntity menuItem = menuMapper.toEntity(request);
        menuItemRepository.save(menuItem);

        if (request.getMenuDetails() != null && !request.getMenuDetails().isEmpty()) {
            // Gộp các nguyên liệu trùng productId & unitId, cộng dồn quantity
            Map<String, Double> mergedDetailMap = new HashMap<>();
            Map<String, CreateMenuItemDetailRequest> detailRequestMap = new HashMap<>();

            for (CreateMenuItemDetailRequest detailRequest : request.getMenuDetails()) {
                String key = detailRequest.getProductId() + "_" + detailRequest.getUnitId();
                mergedDetailMap.put(key, mergedDetailMap.getOrDefault(key, 0.0) + detailRequest.getQuantity());
                // Lưu lại detailRequest cuối cùng để lấy các thông tin khác nếu cần
                detailRequestMap.put(key, detailRequest);
            }

            List<MenuDetailEntity> menuDetailEntityList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : mergedDetailMap.entrySet()) {
                String key = entry.getKey();
                Double quantity = entry.getValue();
                CreateMenuItemDetailRequest detailRequest = detailRequestMap.get(key);

                Integer productId = detailRequest.getProductId();
                Integer unitId = detailRequest.getUnitId();

                ProductEntity product = productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
                UnitEntity unit = unitService.getAllUnits().stream()
                        .filter(u -> u.getId().equals(unitId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn vị tính"));

                MenuKey menuKey = new MenuKey();
                menuKey.setIdProduct(product.getId());
                menuKey.setIdMenuItem(menuItem.getId());
                menuKey.setIdUnit(unit.getId());

                MenuDetailEntity detail = menuMapper.toMenuDetailEntity(detailRequest);
                detail.setProduct(product);
                detail.setMenuItem(menuItem);
                detail.setId(menuKey);
                detail.setUnit(unit);

                // Sửa quantity thành tổng
                detail.setQuantity(quantity);

                menuDetailEntityList.add(detail);
            }
            menuItemDetailRepository.saveAll(menuDetailEntityList);
        }
        return menuItem;
    }

    /**
     * Cập nhật thông tin thực đơn và các chi tiết món liên kết.
     *
     * @param request thông tin cập nhật thực đơn.
     */
    @Transactional
    @Override
    public void updateMenuItem(UpdateMenuItemRequest request) {
        MenuItemEntity menuItem = menuItemRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món thực đơn"));

        // Cập nhật thông tin cơ bản
        menuMapper.updateEntityFromRequest(request, menuItem);
        menuItemRepository.save(menuItem);

        // Xóa các MenuDetailEntity cũ
        menuItemDetailRepository.deleteByMenuItem_Id(menuItem.getId());

        // Thêm mới các MenuDetailEntity từ request (gộp các nguyên liệu trùng productId & unitId, cộng dồn quantity)
        if (request.getMenuDetails() != null && !request.getMenuDetails().isEmpty()) {
            Map<String, Double> mergedDetailMap = new HashMap<>();
            Map<String, CreateMenuItemDetailRequest> detailRequestMap = new HashMap<>();

            for (CreateMenuItemDetailRequest detailRequest : request.getMenuDetails()) {
                String key = detailRequest.getProductId() + "_" + detailRequest.getUnitId();
                mergedDetailMap.put(key, mergedDetailMap.getOrDefault(key, 0.0) + detailRequest.getQuantity());
                detailRequestMap.put(key, detailRequest);
            }

            List<MenuDetailEntity> menuDetailEntityList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : mergedDetailMap.entrySet()) {
                String key = entry.getKey();
                Double quantity = entry.getValue();
                CreateMenuItemDetailRequest detailRequest = detailRequestMap.get(key);

                Integer productId = detailRequest.getProductId();
                Integer unitId = detailRequest.getUnitId();

                ProductEntity product = productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
                UnitEntity unit = unitService.getAllUnits().stream()
                        .filter(u -> u.getId().equals(unitId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn vị tính"));

                MenuKey menuKey = new MenuKey();
                menuKey.setIdProduct(product.getId());
                menuKey.setIdMenuItem(menuItem.getId());
                menuKey.setIdUnit(unit.getId());

                MenuDetailEntity detail = menuMapper.toMenuDetailEntity(detailRequest);
                detail.setProduct(product);
                detail.setMenuItem(menuItem);
                detail.setId(menuKey);
                detail.setUnit(unit);

                detail.setQuantity(quantity);

                menuDetailEntityList.add(detail);
            }
            menuItemDetailRepository.saveAll(menuDetailEntityList);
        }
    }

    /**
     * Xóa mềm một thực đơn (isDeleted = true).
     *
     * @param id id thực đơn cần xóa.
     */
    @Override
    @Transactional
    public void deleteMenuItem(Integer id) {
        MenuItemEntity menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món thực đơn"));
        menuItem.setIsDeleted(true);
        menuItemRepository.save(menuItem);
    }

    /**
     * Lấy thông tin thực đơn theo id.
     *
     * @param id id thực đơn cần lấy thông tin.
     * @return MenuItemEntity theo id.
     */
    @Override
    public MenuItemEntity getMenuItemById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thực đơn có id=" + id));
    }

    /**
     * Kiểm tra tên món đã tồn tại chưa (chưa bị xóa).
     *
     * @param itemName tên món cần kiểm tra.
     * @return true nếu tồn tại, false nếu không tồn tại.
     */
    @Override
    public boolean existsByItemName(String itemName) {
        return menuItemRepository.existsByItemNameAndIsDeletedFalse(itemName);
    }

    /**
     * Kiểm tra tên món đã tồn tại chưa (không tính id hiện tại, chưa bị xóa).
     *
     * @param itemName   tên món cần kiểm tra.
     * @param menuItemId id thực đơn cần loại trừ.
     * @return true nếu tồn tại, false nếu không tồn tại.
     */
    @Override
    public boolean existsByItemNameAndIdNot(String itemName, Integer menuItemId) {
        return menuItemRepository.existsByItemNameAndIsDeletedFalseAndIdNot(itemName, menuItemId);
    }
}