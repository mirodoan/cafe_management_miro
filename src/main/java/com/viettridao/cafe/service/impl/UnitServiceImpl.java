package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.model.UnitEntity;
import com.viettridao.cafe.dto.response.unit.UnitResponse;
import com.viettridao.cafe.repository.UnitRepository;
import com.viettridao.cafe.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UnitServiceImpl
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
 * Triển khai các phương thức xử lý logic liên quan đến đơn vị tính (Unit).
 * Xử lý lấy danh sách, lấy chi tiết, chuyển đổi sang DTO.
 */
@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    // Repository quản lý dữ liệu đơn vị tính
    private final UnitRepository unitRepository;

    /**
     * Lấy danh sách tất cả các đơn vị tính chưa bị xóa.
     *
     * @return Danh sách các đối tượng UnitEntity.
     */
    @Override
    public List<UnitEntity> getAllUnits() {
        return unitRepository.findAllByIsDeleted(false);
    }

    /**
     * Lấy danh sách tất cả các đơn vị tính dưới dạng UnitResponse cho template.
     *
     * @return Danh sách các UnitResponse.
     */
    @Override
    public List<UnitResponse> getAllUnitResponses() {
        return unitRepository.findAllByIsDeleted(false)
                .stream()
                .map(this::toUnitResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin đơn vị tính theo ID.
     *
     * @param id ID của đơn vị tính cần tìm.
     * @return Đối tượng UnitEntity tương ứng với ID.
     * @throws RuntimeException nếu không tìm thấy đơn vị tính với ID đã cho.
     */
    @Override
    public UnitEntity getUnitById(Integer id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị tính có id=" + id));
    }

    /**
     * Chuyển đổi UnitEntity sang UnitResponse.
     */
    private UnitResponse toUnitResponse(UnitEntity entity) {
        UnitResponse response = new UnitResponse();
        response.setId(entity.getId());
        response.setUnitName(entity.getUnitName());
        response.setIsDeleted(entity.getIsDeleted());
        return response;
    }
}