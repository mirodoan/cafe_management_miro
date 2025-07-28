package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.employee.EmployeeResponse;
import com.viettridao.cafe.model.EmployeeEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * EmployeeMapper
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
 * Mapper chuyển đổi giữa EmployeeEntity và EmployeeResponse (DTO).
 * Kết hợp dữ liệu từ Employee, Account, Position.
 */
@Component
@RequiredArgsConstructor
public class EmployeeMapper {
    private final ModelMapper modelMapper;

    /**
     * Chuyển EmployeeEntity sang EmployeeResponse, bổ sung thông tin vị trí và account.
     *
     * @param entity entity Employee cần chuyển.
     * @return EmployeeResponse kết quả.
     */
    public EmployeeResponse toEmployeeResponse(EmployeeEntity entity){
        EmployeeResponse response = new EmployeeResponse();
        modelMapper.map(entity, response);

        if(entity.getPosition() != null){
            response.setPositionId(entity.getPosition().getId());
            response.setPositionName(entity.getPosition().getPositionName());
            response.setSalary(entity.getPosition().getSalary());
        }

        if (entity.getAccount() != null){
            response.setUsername(entity.getAccount().getUsername());
            response.setPassword(entity.getAccount().getPassword());
            response.setImageUrl(entity.getAccount().getImageUrl());
        }
        return response;
    }

    /**
     * Chuyển danh sách EmployeeEntity sang danh sách EmployeeResponse.
     *
     * @param entities danh sách entity Employee.
     * @return danh sách EmployeeResponse.
     */
    public List<EmployeeResponse> toListEmployeeResponse(List<EmployeeEntity> entities){
        return entities.stream().map(this::toEmployeeResponse).toList();
    }
}