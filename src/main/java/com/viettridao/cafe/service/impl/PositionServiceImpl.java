package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.model.PositionEntity;
import com.viettridao.cafe.repository.PositionRepository;
import com.viettridao.cafe.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PositionServiceImpl
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
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    // Repository quản lý dữ liệu chức vụ
    private final PositionRepository positionRepository;

    /**
     * Lấy thông tin chức vụ theo ID.
     *
     * @param id ID của chức vụ cần tìm.
     * @return Đối tượng PositionEntity tương ứng với ID.
     * @throws RuntimeException nếu không tìm thấy chức vụ với ID đã cho.
     */
    @Override
    public PositionEntity getPositionById(Integer id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ id=" + id));
    }

    /**
     * Lấy danh sách tất cả các chức vụ.
     *
     * @return Danh sách các đối tượng PositionEntity.
     */
    @Override
    public List<PositionEntity> getPositions() {
        return positionRepository.getAllPositions();
    }
}