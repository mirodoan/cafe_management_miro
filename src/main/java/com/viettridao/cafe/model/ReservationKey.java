package com.viettridao.cafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
/**
 * Composite key cho ReservationEntity (table_id, employee_id, invoice_id)
 */
public class ReservationKey {
    @Column(name = "table_id")
    private Integer idTable; // ID của bàn liên kết với đặt chỗ

    @Column(name = "employee_id")
    private Integer idEmployee; // ID của nhân viên liên kết với đặt chỗ

    @Column(name = "invoice_id")
    private Integer idInvoice; // ID của hóa đơn liên kết với đặt chỗ

    @Override
    public boolean equals(Object obj) {
        // Kiểm tra xem hai đối tượng có cùng địa chỉ bộ nhớ không (cùng là một đối tượng)
        if (this == obj)
            return true;
        // Nếu đối tượng truyền vào là null hoặc không cùng kiểu class, trả về false
        if (obj == null || getClass() != obj.getClass())
            return false;
        // Ép kiểu đối tượng truyền vào về ReservationKey để so sánh từng trường
        ReservationKey that = (ReservationKey) obj;
        // So sánh lần lượt 3 trường khóa: idTable, idEmployee, idInvoice
        // Nếu cả 3 đều giống nhau thì hai đối tượng bằng nhau (trả về true)
        return java.util.Objects.equals(idTable, that.idTable) &&
                java.util.Objects.equals(idEmployee, that.idEmployee) &&
                java.util.Objects.equals(idInvoice, that.idInvoice);
    }

    @Override
    public int hashCode() {
        // Tạo ra một mã số duy nhất dựa trên 3 trường khóa tổng hợp
        return java.util.Objects.hash(idTable, idEmployee, idInvoice);
    }
}
