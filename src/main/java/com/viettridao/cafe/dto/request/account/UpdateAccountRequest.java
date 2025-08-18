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
    @Size(min = 3, message = "Họ tên tối thiểu 3 ký tự")
    @Size(max = 20, message = "Họ tên tối đa 20 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 - 11 chữ số")
    private String phoneNumber;

    @OptionalSize(min = 5, max = 50, message = "Địa chỉ có độ dài từ 5 đến 50 ký tự nếu được nhập")
    private String address;

    private String imageUrl;

    private Integer positionId;

    private String positionName;

    private Double salary;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, message = "Tên đăng nhập tổi thiểu 3 ký tự")
    @Size(max = 20, message = "Tên đăng nhập tối đa 20 ký tự")
    private String username;

    @OptionalSize(min = 6, max = 20, message = "Mật khẩu mới có độ dài từ 6 đến 20 ký tự nếu được nhập")
    private String password;
}
