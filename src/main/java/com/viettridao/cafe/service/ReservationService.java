package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.sales.CreateReservationRequest;
import com.viettridao.cafe.dto.request.sales.MergeTableRequest;
import com.viettridao.cafe.dto.request.sales.MoveTableRequest;
import com.viettridao.cafe.dto.request.sales.SplitTableRequest;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.TableEntity;

/**
 * ReservationService
 * Interface định nghĩa các nghiệp vụ quản lý đặt bàn và thao tác với bàn trong quán cafe:
 * Đặt bàn mới, gộp bàn, tách bàn, chuyển bàn, lấy reservation theo bàn, và xử lý liên quan khi hủy bàn.
 */
public interface ReservationService {
    /**
     * Gộp nhiều bàn OCCUPIED vào một bàn đích (cộng dồn hóa đơn, cập nhật trạng thái, xóa mềm các bàn nguồn)
     *
     * @param request    MergeTableRequest chứa danh sách bàn và bàn đích
     * @param employeeId ID nhân viên thực hiện thao tác (nếu cần log)
     * @throws IllegalArgumentException nếu validate nghiệp vụ không hợp lệ
     */
    void mergeTables(MergeTableRequest request, Integer employeeId);

    /**
     * Tạo mới một đặt bàn.
     *
     * @param request    Đối tượng chứa thông tin cần thiết để tạo đặt bàn mới.
     * @param employeeId ID của nhân viên thực hiện đặt bàn.
     * @return Thực thể ReservationEntity vừa được tạo.
     */
    ReservationEntity createReservation(CreateReservationRequest request, Integer employeeId);

    /**
     * Lưu đồng bộ reservation, invoice, table khi hủy bàn (xóa mềm)
     *
     * @param reservation ReservationEntity cần cập nhật
     * @param invoice     InvoiceEntity liên quan (có thể null)
     * @param table       TableEntity liên quan (có thể null)
     */
    void saveReservationAndRelated(ReservationEntity reservation, InvoiceEntity invoice, TableEntity table);

    /**
     * Lấy reservation hiện tại (chưa xóa mềm) theo tableId
     *
     * @param tableId id bàn
     * @return ReservationEntity hoặc null nếu không có
     */
    ReservationEntity findCurrentReservationByTableId(Integer tableId);

    /**
     * Tách bàn: chuyển một phần món từ bàn nguồn sang bàn đích
     *
     * @param request    SplitTableRequest chứa thông tin bàn nguồn, bàn đích và các món cần tách
     * @param employeeId ID nhân viên thực hiện thao tác
     * @throws IllegalArgumentException nếu validate nghiệp vụ không hợp lệ
     */
    void splitTable(SplitTableRequest request, Integer employeeId);

    /**
     * Chuyển bàn: chuyển toàn bộ reservation, invoice, invoice detail từ bàn nguồn sang bàn đích
     *
     * @param request    MoveTableRequest chứa id bàn nguồn và bàn đích
     * @param employeeId ID nhân viên thực hiện thao tác
     * @throws IllegalArgumentException nếu validate nghiệp vụ không hợp lệ
     */
    void moveTable(MoveTableRequest request, Integer employeeId);
}