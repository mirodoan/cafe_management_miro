package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.dto.request.equipment.CreateEquipmentRequest;
import com.viettridao.cafe.dto.request.equipment.UpdateEquipmentRequest;
import com.viettridao.cafe.dto.response.equipment.EquipmentPageResponse;
import com.viettridao.cafe.mapper.EquipmentMapper;
import com.viettridao.cafe.model.EquipmentEntity;
import com.viettridao.cafe.repository.EquipmentRepository;
import com.viettridao.cafe.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EquipmentServiceImpl
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
 *
 * Triển khai Service cho thực thể EquipmentEntity.
 * Chịu trách nhiệm xử lý logic nghiệp vụ liên quan đến thiết bị (Equipment).
 */
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    // Repository cho thực thể EquipmentEntity
    private final EquipmentRepository equipmentRepository;

    // Mapper cho thực thể EquipmentEntity
    private final EquipmentMapper equipmentMapper;

    /**
     * Lấy danh sách tất cả thiết bị (không phân trang).
     *
     * @return Danh sách các thực thể EquipmentEntity.
     */
    @Override
    public List<EquipmentEntity> getAllEquipments() {
        return equipmentRepository.getAllEquipments();
    }

    /**
     * Tạo mới một thiết bị.
     *
     * @param request Đối tượng chứa thông tin cần thiết để tạo thiết bị mới.
     * @return Thực thể EquipmentEntity vừa được tạo.
     */
    @Transactional
    @Override
    public EquipmentEntity createEquipment(CreateEquipmentRequest request) {
        EquipmentEntity equipmentEntity = new EquipmentEntity();
        equipmentEntity.setEquipmentName(request.getEquipmentName());
        equipmentEntity.setQuantity(request.getQuantity());
        equipmentEntity.setPurchaseDate(request.getPurchaseDate());
        equipmentEntity.setPurchasePrice(request.getPurchasePrice());
        equipmentEntity.setIsDeleted(false);

        return equipmentRepository.save(equipmentEntity);
    }

    /**
     * Xóa mềm một thiết bị (đặt isDeleted = true).
     *
     * @param id ID của thiết bị cần xóa.
     */
    @Transactional
    @Override
    public void deleteEquipment(Integer id) {
        EquipmentEntity equipment = getEquipmentById(id);
        equipment.setIsDeleted(true);
        equipmentRepository.save(equipment);
    }

    /**
     * Lấy thông tin chi tiết của một thiết bị dựa trên ID.
     *
     * @param id ID của thiết bị cần lấy thông tin.
     * @return Thực thể EquipmentEntity tương ứng với ID.
     */
    @Override
    public EquipmentEntity getEquipmentById(Integer id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị có id=" + id));
    }

    /**
     * Cập nhật thông tin thiết bị.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho thiết bị.
     */
    @Transactional
    @Override
    public void updateEquipment(UpdateEquipmentRequest request) {
        EquipmentEntity equipmentEntity = getEquipmentById(request.getId());
        equipmentEntity.setEquipmentName(request.getEquipmentName());
        equipmentEntity.setQuantity(request.getQuantity());
        equipmentEntity.setPurchaseDate(request.getPurchaseDate());
        equipmentEntity.setPurchasePrice(request.getPurchasePrice());

        equipmentRepository.save(equipmentEntity);
    }

    /**
     * Lấy danh sách tất cả thiết bị có phân trang.
     *
     * @param page Số trang cần lấy.
     * @param size Số lượng bản ghi trên mỗi trang.
     * @return Đối tượng EquipmentPageResponse chứa danh sách thiết bị và thông tin phân trang.
     */
    @Override
    public EquipmentPageResponse getAllEquipmentsPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<EquipmentEntity> equipmentEntities = equipmentRepository.getAllEquipmentsByPage(pageable);

        EquipmentPageResponse equipmentPageResponse = new EquipmentPageResponse();
        equipmentPageResponse.setPageNumber(equipmentEntities.getNumber());
        equipmentPageResponse.setTotalElements(equipmentEntities.getTotalElements());
        equipmentPageResponse.setTotalPages(equipmentEntities.getTotalPages());
        equipmentPageResponse.setPageSize(equipmentEntities.getSize());
        equipmentPageResponse.setEquipments(equipmentMapper.toEquipmentResponseList(equipmentEntities.getContent()));

        return equipmentPageResponse;
    }
}