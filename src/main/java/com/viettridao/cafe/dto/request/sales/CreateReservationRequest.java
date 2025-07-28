package com.viettridao.cafe.dto.request.sales;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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
    @Pattern(regexp = "^\\d{10,15}$", message = "Số điện thoại phải từ 10 đến 15 chữ số")
    private String customerPhone; // Số điện thoại khách hàng

    @NotNull(message = "Ngày giờ đặt bàn không được để trống")
    @Future(message = "Ngày giờ đặt bàn phải là thời gian trong tương lai")
    private LocalDateTime reservationDate; // Ngày giờ đặt bàn
}
