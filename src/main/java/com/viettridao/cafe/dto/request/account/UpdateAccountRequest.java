package com.viettridao.cafe.dto.request.account;

import com.viettridao.cafe.exception.OptionalSize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountRequest {
    private Integer id;

    @NotBlank(message = "Họ tên không được để trống")
    @OptionalSize(min = 5, message = "Họ tên ít nhất 5 ký tự nếu được nhập")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại phải bắt đầu bằng số 0 và số điện thoại 10-11 chữ số")
    private String phoneNumber;

    @OptionalSize(min = 5, message = "Địa chỉ tối thiểu 5 ký tự nếu được nhập")
    private String address;

    private String imageUrl;

    private Integer positionId;

    private String positionName;

    private Double salary;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, message = "Tên đăng nhập tổi thiểu 3 ký tự")
    private String username;

    @OptionalSize(min = 6, message = "Mật khẩu tối thiểu 6 ký tự nếu được nhập")
    private String password;
}
