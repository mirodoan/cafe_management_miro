package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.equipment.EquipmentResponse;
import com.viettridao.cafe.dto.response.expense.BudgetResponse;
import com.viettridao.cafe.model.EquipmentEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * EquipmentMapper
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
 * Mapper chuyển đổi giữa EquipmentEntity và các DTO (EquipmentResponse, BudgetResponse).
 * Hỗ trợ chuyển đổi bằng tay và qua thư viện ModelMapper.
 */
@Component
@RequiredArgsConstructor
public class EquipmentMapper {
    private final ModelMapper modelMapper;

    /**
     * Manual mapping: chuyển đổi EquipmentEntity sang EquipmentResponse.
     *
     * @param entity EquipmentEntity nguồn.
     * @return EquipmentResponse
     */
    public EquipmentResponse toResponse(EquipmentEntity entity) {
        EquipmentResponse equipmentResponse = new EquipmentResponse();
        equipmentResponse.setId(entity.getId());
        equipmentResponse.setEquipmentName(entity.getEquipmentName());
        equipmentResponse.setNotes(entity.getNotes());
        equipmentResponse.setPurchaseDate(entity.getPurchaseDate());
        equipmentResponse.setPurchasePrice(entity.getPurchasePrice());
        equipmentResponse.setIsDeleted(entity.getIsDeleted());
        equipmentResponse.setQuantity(entity.getQuantity());

        return equipmentResponse;
    }

    /**
     * Mapping bằng ModelMapper: chuyển đổi EquipmentEntity sang EquipmentResponse.
     *
     * @param entity EquipmentEntity nguồn.
     * @return EquipmentResponse
     */
    public EquipmentResponse toEquipmentResponse(EquipmentEntity entity) {
        EquipmentResponse equipmentResponse = new EquipmentResponse();
        modelMapper.map(entity, equipmentResponse);

        return equipmentResponse;
    }

    /**
     * Manual mapping: chuyển đổi list EquipmentEntity sang list EquipmentResponse.
     *
     * @param entities danh sách EquipmentEntity.
     * @return danh sách EquipmentResponse.
     */
    public List<EquipmentResponse> toResponseList(List<EquipmentEntity> entities){
        return entities.stream().map(this::toResponse).toList();
    }

    /**
     * Mapping bằng ModelMapper: chuyển đổi list EquipmentEntity sang list EquipmentResponse.
     *
     * @param entities danh sách EquipmentEntity.
     * @return danh sách EquipmentResponse.
     */
    public List<EquipmentResponse> toEquipmentResponseList(List<EquipmentEntity> entities) {
        return entities.stream().map(this::toEquipmentResponse).toList();
    }

    /**
     * Mapping sang BudgetResponse (dùng cho thống kê chi tiêu từ mua thiết bị).
     *
     * @param entity EquipmentEntity nguồn.
     * @return BudgetResponse
     */
    public BudgetResponse toBudgetDto(EquipmentEntity entity) {
        if (entity == null) {
            return null;
        }

        BudgetResponse dto = new BudgetResponse();
        dto.setDate(entity.getPurchaseDate());
        dto.setExpense(entity.getPurchasePrice());
        dto.setIncome(0.0);
        return dto;
    }
}