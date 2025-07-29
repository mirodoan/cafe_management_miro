package com.viettridao.cafe.dto.request.sales;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateReservationRequest {
    @NotNull(message = "ID bàn không được để trống")
    @Min(value = 1, message = "ID bàn phải lớn hơn 0")
    private Integer tableId; // ID bàn được chọn

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(min = 3, message = "Tên khách hàng tối thiểu 3 ký tự")
    private String customerName; // Tên khách hàng

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại phải từ 10 đến 11 chữ số")
    private String customerPhone; // Số điện thoại khách hàng

    @NotNull(message = "Ngày giờ đặt bàn không được để trống")
    @Future(message = "Ngày giờ đặt bàn phải là thời gian trong tương lai")
    private LocalDateTime reservationDate; // Ngày giờ đặt bàn
}
