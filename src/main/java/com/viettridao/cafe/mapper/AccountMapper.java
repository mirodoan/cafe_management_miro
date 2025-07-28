package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.account.AccountResponse;
import com.viettridao.cafe.model.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AccountMapper
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
 * 19-07-2025   mirodoan    Create
 *
 * Mapper chuyển đổi giữa AccountEntity và AccountResponse (DTO).
 * Kết hợp dữ liệu từ Account + Employee + Position.
 */
@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final ModelMapper modelMapper;

    /**
     * Chuyển AccountEntity sang AccountResponse, bổ sung thông tin nhân viên và chức vụ.
     *
     * @param entity entity Account cần chuyển.
     * @return AccountResponse kết quả.
     */
    public AccountResponse toAccountResponse(AccountEntity entity){
        AccountResponse response = new AccountResponse();
        modelMapper.map(entity, response);

        if(entity.getEmployee() != null){
            response.setFullName(entity.getEmployee().getFullName());
            response.setAddress(entity.getEmployee().getAddress());
            response.setPhoneNumber(entity.getEmployee().getPhoneNumber());

            if(entity.getEmployee().getPosition() != null){
                response.setSalary(entity.getEmployee().getPosition().getSalary());
                response.setPositionName(entity.getEmployee().getPosition().getPositionName());
                response.setPositionId(entity.getEmployee().getPosition().getId());
            }
        }
        return response;
    }

    /**
     * Chuyển danh sách AccountEntity sang danh sách AccountResponse.
     *
     * @param entities danh sách entity Account.
     * @return danh sách AccountResponse.
     */
    public List<AccountResponse> toListAccountResponse(List<AccountEntity> entities) {
        return entities.stream()
                .map(this::toAccountResponse)
                .toList();
    }
}