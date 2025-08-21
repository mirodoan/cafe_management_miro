package com.viettridao.cafe.dto.request.sales;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateReservationRequest {
    @NotNull(message = "ID bàn không được để trống")
    @Min(value = 1, message = "ID bàn phải lớn hơn 0")
    private Integer tableId; // ID bàn được chọn

    private String tableName; // Thêm tên bàn để hiển thị trong UI

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(min = 3, message = "Tên khách hàng tối thiểu 3 ký tự")
    @Size(max = 20, message = "Tên khách hàng tối đa 20 ký tự")
    private String customerName; // Tên khách hàng

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại phải bắt đầu từ 0 và có từ 10 đến 11 chữ số")
    private String customerPhone; // Số điện thoại khách hàng

    @NotNull(message = "Thời gian đặt bàn không được để trống")
    @Future(message = "Thời gian đặt bàn phải là thời gian trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime reservationDateTime; // Ngày giờ đặt bàn
}
