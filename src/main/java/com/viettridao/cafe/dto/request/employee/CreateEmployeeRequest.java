package com.viettridao.cafe.dto.request.employee;

import com.viettridao.cafe.exception.OptionalSize;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho việc tạo mới thông tin nhân viên.
 * Bao gồm các trường như tên, số điện thoại, địa chỉ, chức vụ và thông tin đăng
 * nhập.
 */
@Getter
@Setter
public class CreateEmployeeRequest {

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(min = 5, message = "Tên nhân viên tối thiểu 5 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10-11 chữ số")
    private String phoneNumber;

    @OptionalSize(min = 5, message = "Địa chỉ tối thiểu 5 ký tự nếu được nhập")
    private String address;

    private String imageUrl;

    @NotNull(message = "Chức vụ không được để trống")
    @Min(value = 1, message = "ID chức vụ phải lớn hơn 0")
    private Integer positionId;

    private String positionName;

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, message = "Username tối thiểu 3 ký tự")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password tối thiểu 6 ký tự")
    private String password;
}
