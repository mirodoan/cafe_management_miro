package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.sales.CreateReservationRequest;
import com.viettridao.cafe.dto.request.sales.MergeTableRequest;
import com.viettridao.cafe.dto.request.sales.MoveTableRequest;
import com.viettridao.cafe.dto.request.sales.SplitTableRequest;
import com.viettridao.cafe.dto.response.sales.OrderDetailRessponse;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.TableEntity;

import java.util.Map;

/**
 * ReservationService
 * Interface định nghĩa các nghiệp vụ quản lý đặt bàn và thao tác với bàn trong quán cafe:
 * Đặt bàn mới, gộp bàn, tách bàn, chuyển bàn, lấy reservation theo bàn, và xử lý liên quan khi hủy bàn.
 */
public interface ReservationService {
    /**
     * Lấy chi tiết order (DTO) của một bàn theo tableId
     *
     * @param tableId ID bàn
     * @return OrderDetailRessponse
     * @throws IllegalArgumentException nếu không tìm thấy bàn hoặc không có reservation
     */
    OrderDetailRessponse getOrderDetailByTableId(Integer tableId);

    /**
     * Lấy thông tin chi tiết thanh toán cho 1 bàn (dùng cho modal thanh toán)
     *
     * @param tableId ID bàn
     * @return OrderDetailRessponse
     * @throws IllegalArgumentException nếu không hợp lệ
     */
    OrderDetailRessponse getPaymentInfoForTable(Integer tableId);

    /**
     * Tính toán và cập nhật lại tổng tiền cho hóa đơn dựa trên các chi tiết hóa đơn còn hiệu lực.
     * Chỉ tính các món chưa bị xóa mềm và quantity > 0.
     * Nên gọi hàm này sau mỗi lần thêm/xóa/cập nhật món (InvoiceDetail) trong hóa đơn.
     *
     * @param invoice Hóa đơn cần cập nhật tổng tiền
     */
    void updateInvoiceTotalAmount(InvoiceEntity invoice);


    /**
     * Thực hiện xác nhận thanh toán cho bàn
     *
     * @param tableId ID bàn
     * @throws IllegalArgumentException nếu không hợp lệ
     */
    void payInvoiceForTable(Integer tableId);

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
     * Xử lý nghiệp vụ hủy bàn: xóa mềm reservation, invoice và chuyển bàn về AVAILABLE.
     *
     * @param tableId ID bàn cần hủy đặt
     * @throws IllegalArgumentException nếu không hợp lệ (không tìm thấy reservation, không đúng trạng thái)
     */
    void cancelReservation(Integer tableId);

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
     * Chuẩn bị dữ liệu cho form tách bàn: kiểm tra nghiệp vụ và trả về các thông tin cần thiết để hiển thị form tách bàn.
     *
     * @param sourceTableId ID của bàn nguồn muốn tách món
     * @return Map chứa dữ liệu cho form tách bàn (bao gồm: sourceTable, availableTables, occupiedTables, sourceInvoiceDetails, selectedTableId, splitTableRequest, ...)
     * @throws IllegalArgumentException nếu có lỗi nghiệp vụ (bàn không tồn tại, không hợp lệ, không có món để tách, ...)
     */
    Map<String, Object> prepareSplitTableForm(Integer sourceTableId);

    /**
     * Chuyển bàn: chuyển toàn bộ reservation, invoice, invoice detail từ bàn nguồn sang bàn đích
     *
     * @param request    MoveTableRequest chứa id bàn nguồn và bàn đích
     * @param employeeId ID nhân viên thực hiện thao tác
     * @throws IllegalArgumentException nếu validate nghiệp vụ không hợp lệ
     */
    void moveTable(MoveTableRequest request, Integer employeeId);

    /**
     * Chuẩn bị dữ liệu cho form chuyển bàn: kiểm tra và trả về các thông tin cần thiết.
     *
     * @param sourceTableId ID bàn nguồn
     * @return Map chứa dữ liệu cho form (sourceTable, availableTables, tất cả tables,...)
     * @throws IllegalArgumentException nếu gặp lỗi nghiệp vụ
     */
    Map<String, Object> prepareMoveTableForm(Integer sourceTableId);
}