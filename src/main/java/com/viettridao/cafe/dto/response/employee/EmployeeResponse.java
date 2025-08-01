package com.viettridao.cafe.dto.response.employee;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO Response cho thông tin nhân viên - Optimized data transfer object cho
 */
@Getter
@Setter
public class EmployeeResponse {

    private Integer id;

    private String fullName;

    private String phoneNumber;

    private String address;

    private String imageUrl;

    private Integer positionId;

    private String positionName;

    private Double salary;

    private String username;

    private String password;
}
