package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.equipment.CreateEquipmentRequest;
import com.viettridao.cafe.dto.request.equipment.UpdateEquipmentRequest;
import com.viettridao.cafe.dto.response.equipment.EquipmentPageResponse;
import com.viettridao.cafe.model.EquipmentEntity;
import java.util.List;

/**
 * EquipmentService
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
public interface EquipmentService {

    /**
     * Lấy danh sách tất cả thiết bị (không phân trang).
     *
     * @return Danh sách các thực thể EquipmentEntity.
     */
    List<EquipmentEntity> getAllEquipments();

    /**
     * Lấy danh sách tất cả thiết bị có phân trang.
     *
     * @param page Số trang cần lấy.
     * @param size Số lượng bản ghi trên mỗi trang.
     * @return Đối tượng EquipmentPageResponse chứa danh sách thiết bị và thông tin phân trang.
     */
    EquipmentPageResponse getAllEquipmentsPage(int page, int size);

    /**
     * Tạo mới một thiết bị.
     *
     * @param request Đối tượng chứa thông tin cần thiết để tạo thiết bị mới.
     * @return Thực thể EquipmentEntity vừa được tạo.
     */
    EquipmentEntity createEquipment(CreateEquipmentRequest request);

    /**
     * Xóa một thiết bị dựa trên ID.
     *
     * @param id ID của thiết bị cần xóa.
     */
    void deleteEquipment(Integer id);

    /**
     * Lấy thông tin chi tiết của một thiết bị dựa trên ID.
     *
     * @param id ID của thiết bị cần lấy thông tin.
     * @return Thực thể EquipmentEntity tương ứng với ID.
     */
    EquipmentEntity getEquipmentById(Integer id);

    /**
     * Cập nhật thông tin thiết bị.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho thiết bị.
     */
    void updateEquipment(UpdateEquipmentRequest request);
}