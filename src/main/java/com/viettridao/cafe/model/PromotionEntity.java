package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "promotions") // khuyenmai
public class PromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Integer id; // Khóa chính của bảng promotions

    @Column(name = "promotion_name")
    private String promotionName; // Tên của chương trình khuyến mãi

    @Column(name = "start_date")
    private LocalDate startDate; // Ngày bắt đầu của chương trình khuyến mãi

    @Column(name = "end_date")
    private LocalDate endDate; // Ngày kết thúc của chương trình khuyến mãi

    @Column(name = "discount_value")
    private Double discountValue; // Giá trị giảm giá của chương trình khuyến mãi

    @Column(name = "status")
    private Boolean status; // Trạng thái của chương trình khuyến mãi (hoạt động hoặc không)

    @Column(name = "description")
    private String description; // Mô tả chi tiết về chương trình khuyến mãi

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete) của chương trình khuyến mãi

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    private List<InvoiceEntity> invoices; // Danh sách hóa đơn liên kết với chương trình khuyến mãi
}
