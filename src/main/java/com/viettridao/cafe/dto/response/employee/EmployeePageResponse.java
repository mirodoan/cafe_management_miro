package com.viettridao.cafe.dto.response.employee;

import com.viettridao.cafe.dto.response.PageResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO cho phản hồi phân trang dữ liệu nhân viên.
 * Kế thừa lớp PageResponse tổng quát.
 */
@Getter
@Setter
public class EmployeePageResponse extends PageResponse {
    private List<EmployeeResponse> employees;
}
