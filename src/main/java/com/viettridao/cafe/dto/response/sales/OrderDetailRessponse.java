package com.viettridao.cafe.dto.response.sales;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO trả về chi tiết order/bàn cho màn hình xem bàn hoặc sau khi chọn món.
 */
@Getter
@Setter
public class OrderDetailRessponse {
    private Integer tableId;
    private String tableName; // Thêm tên bàn để hiển thị trong UI
    private String tableStatus;
    private Integer invoiceId;
    private String invoiceStatus;
    private String customerName; // Có thể null nếu không đặt trước
    private String customerPhone; // Có thể null nếu không đặt trước
    private LocalDateTime reservationDate; // Có thể null nếu không đặt trước
    private List<MenuOrderItemDetail> items; // Có thể null hoặc rỗng nếu chưa gọi món
    private Double totalAmount; // Tổng tiền hóa đơn

    @Getter
    @Setter
    public static class MenuOrderItemDetail {
        private Integer menuItemId;
        private String menuItemName;
        private Integer quantity;
        private Double price;
        private Double amount; // Thành tiền (quantity * price)
    }
}
