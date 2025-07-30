package com.viettridao.cafe.service.impl;

// Các import thư viện, entity, repository, service cần thiết

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.sales.CreateReservationRequest;
import com.viettridao.cafe.dto.request.sales.MergeTableRequest;
import com.viettridao.cafe.dto.request.sales.MoveTableRequest;
import com.viettridao.cafe.dto.request.sales.SplitTableRequest;
import com.viettridao.cafe.dto.response.sales.OrderDetailRessponse;
import com.viettridao.cafe.mapper.OrderDetailMapper;
import com.viettridao.cafe.model.*;
import com.viettridao.cafe.repository.*;
import com.viettridao.cafe.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Triển khai các nghiệp vụ quản lý đặt bàn, gộp bàn, tách bàn, chuyển bàn cho hệ thống quản lý quán cafe.
 * Bao gồm các thao tác tạo mới đặt bàn, lưu trạng thái liên quan, tìm kiếm, gộp/tách/chuyển bàn và đồng bộ hóa đơn, reservation với trạng thái bàn.
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    // Khai báo các repository dùng để thao tác dữ liệu
    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final InvoiceRepository invoiceRepository;
    private final EmployeeRepository employeeRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final OrderDetailMapper orderDetailMapper;

    /**
     * Tìm reservation hiện tại (chưa xóa mềm) theo tableId
     *
     * @param tableId id bàn
     * @return ReservationEntity hoặc null nếu không có
     */
    @Override
    public ReservationEntity findCurrentReservationByTableId(Integer tableId) {
        // Tìm reservation chưa xóa mềm theo tableId
        Optional<ReservationEntity> result = reservationRepository.findCurrentReservationByTableId(tableId);
        return result.orElse(null);
    }

    /**
     * Lưu đồng bộ reservation, invoice, table khi hủy bàn (xóa mềm)
     */
    @Override
    @Transactional
    public void saveReservationAndRelated(ReservationEntity reservation,
                                          InvoiceEntity invoice, TableEntity table) {
        // Kiểm tra reservation
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation không được null!");
        }

        // Lưu trạng thái mới của reservation
        reservationRepository.save(reservation);

        // Nếu có invoice thì lưu lại
        if (invoice != null) {
            invoiceRepository.save(invoice);
        }

        // Nếu có table thì lưu lại
        if (table != null) {
            tableRepository.save(table);
        }
    }

    /**
     * Lấy chi tiết order (OrderDetailRessponse) của một bàn theo tableId.
     *
     * @param tableId ID bàn cần xem chi tiết
     * @return Thông tin chi tiết order dưới dạng DTO
     * @throws IllegalArgumentException nếu không tìm thấy bàn hoặc không có reservation
     */
    @Override
    public OrderDetailRessponse getOrderDetailByTableId(Integer tableId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với ID: " + tableId));

        ReservationEntity reservation = findCurrentReservationByTableId(tableId);
        if (reservation == null) {
            throw new IllegalArgumentException("Không có thông tin đặt bàn/order cho bàn này!");
        }

        InvoiceEntity invoice = reservation.getInvoice();
        List<InvoiceDetailEntity> invoiceDetails =
                invoiceDetailRepository.findAllByInvoice_IdAndIsDeletedFalse(invoice.getId());

        return orderDetailMapper.toOrderDetailResponse(table, invoice, reservation, invoiceDetails);
    }

    /**
     * Lấy thông tin chi tiết phục vụ modal thanh toán cho một bàn OCCUPIED.
     *
     * @param tableId ID bàn cần thanh toán
     * @return Thông tin chi tiết order (OrderDetailRessponse) dùng cho modal thanh toán
     * @throws IllegalArgumentException nếu bàn không OCCUPIED, không có reservation hoặc không có hóa đơn
     */
    @Override
    public OrderDetailRessponse getPaymentInfoForTable(Integer tableId) {
        // Lấy thông tin bàn
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với ID: " + tableId));
        if (table.getStatus() != TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Chỉ có thể thanh toán bàn đang sử dụng (OCCUPIED)!");
        }

        // Tìm reservation hiện tại của bàn
        ReservationEntity reservation = findCurrentReservationByTableId(tableId);
        if (reservation == null) {
            throw new IllegalArgumentException("Không có thông tin đặt bàn để thanh toán!");
        }
        // Lấy hóa đơn hiện tại
        InvoiceEntity invoice = reservation.getInvoice();
        if (invoice == null) {
            throw new IllegalArgumentException("Không có hóa đơn để thanh toán!");
        }
        // Lấy chi tiết hóa đơn
        List<InvoiceDetailEntity> invoiceDetails = invoiceDetailRepository.findAllByInvoice_IdAndIsDeletedFalse(invoice.getId());
        // Map sang DTO
        return orderDetailMapper.toOrderDetailResponse(table, invoice, reservation, invoiceDetails);
    }

    /**
     * Thực hiện xác nhận thanh toán cho bàn: cập nhật trạng thái hóa đơn, xóa mềm reservation và chuyển bàn về AVAILABLE.
     *
     * @param tableId ID bàn cần thanh toán
     * @throws IllegalArgumentException nếu không tìm thấy bàn, reservation hoặc hóa đơn
     */
    @Override
    @Transactional
    public void payInvoiceForTable(Integer tableId) {
        // Lấy thông tin bàn
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với ID: " + tableId));
        // Tìm reservation hiện tại của bàn
        ReservationEntity reservation = findCurrentReservationByTableId(tableId);
        if (reservation == null) {
            throw new IllegalArgumentException("Không có thông tin đặt bàn để thanh toán!");
        }
        // Lấy hóa đơn hiện tại
        InvoiceEntity invoice = reservation.getInvoice();
        if (invoice == null) {
            throw new IllegalArgumentException("Không có hóa đơn để thanh toán!");
        }
        // Đổi trạng thái hóa đơn, đặt bàn, bàn khi thanh toán
        invoice.setStatus(InvoiceStatus.PAID);
        reservation.setIsDeleted(true);
        table.setStatus(TableStatus.AVAILABLE);
        saveReservationAndRelated(reservation, invoice, table);
    }

    /**
     * Tạo mới một đặt bàn.
     *
     * @param request    Đối tượng chứa thông tin cần thiết để tạo đặt bàn mới.
     * @param employeeId ID của nhân viên thực hiện đặt bàn.
     * @return Thực thể ReservationEntity vừa được tạo.
     */
    @Override
    @Transactional
    public ReservationEntity createReservation(CreateReservationRequest request, Integer employeeId) {
        // Kiểm tra giờ hiện tại (backend chặn user cố tình gửi request sau 21h)
        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.of(21, 0)) || now.equals(LocalTime.of(21, 0))) {
            throw new IllegalArgumentException("Quán sắp đóng cửa, không nhận đặt bàn sau 21h. Vui lòng quay lại vào hôm sau!");
        }

        // Kiểm tra giờ đặt bàn hợp lệ (trong khung 8:00-21:00)
        if (!isValidReservationTime(request.getReservationDateTime())) {
            throw new IllegalArgumentException("Chỉ được đặt bàn từ 8:00 đến trước 21:00 trong ngày.");
        }

        // 1. Tìm bàn theo ID và kiểm tra trạng thái còn AVAILABLE không
        TableEntity table = tableRepository.findById(request.getTableId())
                .filter(t -> t.getStatus() == TableStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại hoặc không khả dụng."));

        // 2. Tìm nhân viên theo ID, đảm bảo nhân viên tồn tại
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại."));

        // 3. Tạo mới một hóa đơn (invoice), set trạng thái là RESERVED, lưu vào database
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setStatus(InvoiceStatus.RESERVED);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setIsDeleted(false);
        invoiceRepository.save(invoice);

        // 4. Tạo mới ReservationEntity, thiết lập các trường liên quan
        ReservationEntity reservation = new ReservationEntity();
        ReservationKey reservationKey = new ReservationKey();
        reservationKey.setIdTable(table.getId());
        reservationKey.setIdEmployee(employee.getId());
        reservationKey.setIdInvoice(invoice.getId());

        reservation.setId(reservationKey);
        reservation.setTable(table);
        reservation.setEmployee(employee);
        reservation.setCustomerName(request.getCustomerName());
        reservation.setCustomerPhone(request.getCustomerPhone());
        reservation.setReservationDate(request.getReservationDateTime());
        reservation.setInvoice(invoice);
        reservation.setIsDeleted(false);

        // 5. Lưu thông tin reservation vào database
        reservationRepository.save(reservation);

        // 6. Cập nhật trạng thái bàn thành RESERVED và lưu lại
        table.setStatus(TableStatus.RESERVED);
        tableRepository.save(table);

        // 7. Trả về reservation vừa tạo
        return reservation;
    }

    /**
     * Kiểm tra thời gian đặt bàn có hợp lệ không (từ 8h đến trước 21h).
     */
    private boolean isValidReservationTime(LocalDateTime reservationDate) {
        int hour = reservationDate.getHour();
        return hour >= 8 && hour < 21;
    }

    /**
     * Xử lý nghiệp vụ hủy bàn: xóa mềm reservation và invoice, chuyển bàn về AVAILABLE.
     *
     * @param tableId ID bàn cần hủy đặt
     * @throws IllegalArgumentException nếu không hợp lệ (không tìm thấy reservation, không đúng trạng thái)
     */
    @Override
    @Transactional
    public void cancelReservation(Integer tableId) {
        // Lấy thông tin reservation hiện tại
        ReservationEntity reservation = findCurrentReservationByTableId(tableId);
        if (reservation == null || Boolean.TRUE.equals(reservation.getIsDeleted())) {
            throw new IllegalArgumentException("Không tìm thấy thông tin đặt bàn để hủy!");
        }
        if (reservation.getTable().getStatus() != TableStatus.RESERVED) {
            throw new IllegalArgumentException("Chỉ có thể hủy bàn ở trạng thái ĐÃ ĐẶT!");
        }
        // Xóa mềm reservation và invoice (chưa có invoice detail)
        reservation.setIsDeleted(true);
        InvoiceEntity invoice = reservation.getInvoice();
        if (invoice != null) {
            invoice.setIsDeleted(true);
            invoiceRepository.save(invoice);
        }
        // Đổi trạng thái bàn về AVAILABLE
        TableEntity table = reservation.getTable();
        table.setStatus(TableStatus.AVAILABLE);
        saveReservationAndRelated(reservation, invoice, table); // đã save reservation
    }

    /**
     * Gộp nhiều bàn OCCUPIED vào một bàn đích (cộng dồn hóa đơn, cập nhật trạng thái, xóa mềm các bàn nguồn)
     */
    @Override
    @Transactional
    public void mergeTables(MergeTableRequest request, Integer employeeId) {
        // Lấy danh sách các bàn cần gộp và bàn đích
        List<Integer> tableIds = request.getTableIds();
        Integer targetTableId = request.getTargetTableId();
        // Kiểm tra số lượng bàn cần gộp
        if (tableIds == null || tableIds.size() < 2)
            throw new IllegalArgumentException("Phải chọn ít nhất 2 bàn để gộp");
        // Kiểm tra bàn đích có nằm trong danh sách chọn không
        if (!tableIds.contains(targetTableId))
            throw new IllegalArgumentException("Bàn gộp đến phải nằm trong danh sách bàn đã chọn");

        // Lấy thông tin các bàn từ database
        List<TableEntity> tables = tableRepository.findAllById(tableIds);
        if (tables.size() != tableIds.size())
            throw new IllegalArgumentException("Có bàn không tồn tại");
        // Kiểm tra trạng thái tất cả các bàn đều đang OCCUPIED
        for (TableEntity t : tables) {
            if (t.getStatus() != TableStatus.OCCUPIED)
                throw new IllegalArgumentException("Chỉ được gộp các bàn đang sử dụng (OCCUPIED)");
        }

        // Tìm bàn đích trong danh sách
        TableEntity targetTable = tables.stream().filter(t -> t.getId().equals(targetTableId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn gộp đến"));

        // Tìm reservation và invoice của bàn đích
        ReservationEntity targetReservation = reservationRepository.findCurrentReservationByTableId(targetTableId)
                .orElse(null);
        if (targetReservation == null)
            throw new IllegalArgumentException("Không tìm thấy reservation bàn gộp đến");
        InvoiceEntity targetInvoice = targetReservation.getInvoice();
        if (targetInvoice == null)
            throw new IllegalArgumentException("Không tìm thấy hóa đơn bàn gộp đến");

        // Tạo map để lưu các chi tiết hóa đơn của bàn đích theo menuItemId
        Map<Integer, InvoiceDetailEntity> targetDetailMap = new HashMap<>();
        List<InvoiceDetailEntity> targetDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(targetInvoice.getId());
        for (InvoiceDetailEntity detail : targetDetails) {
            targetDetailMap.put(detail.getMenuItem().getId(), detail);
        }

        // Duyệt qua các bàn nguồn để cộng dồn hóa đơn vào bàn đích
        for (TableEntity srcTable : tables) {
            // Bỏ vào bàn đích
            if (srcTable.getId().equals(targetTableId))
                continue;
            // Tìm reservation và invoice của bàn nguồn
            ReservationEntity srcReservation = reservationRepository.findCurrentReservationByTableId(srcTable.getId())
                    .orElse(null);
            if (srcReservation == null)
                continue;
            InvoiceEntity srcInvoice = srcReservation.getInvoice();
            if (srcInvoice == null)
                continue;
            // Đánh dấu trạng thái hóa đơn nguồn là UNDER_REVIEW
            srcInvoice.setStatus(InvoiceStatus.UNDER_REVIEW);
            invoiceRepository.save(srcInvoice);

            // Lấy các chi tiết hóa đơn của bàn nguồn
            List<InvoiceDetailEntity> srcDetails = invoiceDetailRepository
                    .findAllByInvoice_IdAndIsDeletedFalse(srcInvoice.getId());
            for (InvoiceDetailEntity srcDetail : srcDetails) {
                Integer menuItemId = srcDetail.getMenuItem().getId();
                InvoiceDetailEntity targetDetail = targetDetailMap.get(menuItemId);
                // Nếu bàn đích đã có món này thì cộng dồn số lượng
                if (targetDetail != null) {
                    targetDetail.setQuantity(targetDetail.getQuantity() + srcDetail.getQuantity());
                    invoiceDetailRepository.save(targetDetail);
                } else {
                    // Nếu chưa có thì tạo mới một chi tiết hóa đơn
                    InvoiceDetailEntity newDetail = new InvoiceDetailEntity();
                    com.viettridao.cafe.model.InvoiceKey newKey = new com.viettridao.cafe.model.InvoiceKey();
                    newKey.setIdInvoice(targetInvoice.getId());
                    newKey.setIdMenuItem(menuItemId);
                    newDetail.setId(newKey);
                    newDetail.setInvoice(targetInvoice);
                    newDetail.setMenuItem(srcDetail.getMenuItem());
                    newDetail.setQuantity(srcDetail.getQuantity());
                    newDetail.setPrice(srcDetail.getPrice());
                    newDetail.setIsDeleted(false);
                    invoiceDetailRepository.save(newDetail);
                    targetDetailMap.put(menuItemId, newDetail);
                }
                // Đánh dấu chi tiết hóa đơn nguồn là đã xóa mềm
                srcDetail.setIsDeleted(true);
                invoiceDetailRepository.save(srcDetail);
            }
            // Đánh dấu hóa đơn nguồn là đã hủy và xóa mềm
            srcInvoice.setStatus(InvoiceStatus.CANCELLED);
            srcInvoice.setIsDeleted(true);
            invoiceRepository.save(srcInvoice);

            // Đánh dấu reservation nguồn là đã xóa mềm
            srcReservation.setIsDeleted(true);
            reservationRepository.save(srcReservation);

            // Đổi trạng thái bàn nguồn thành AVAILABLE
            srcTable.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(srcTable);
        }
        // Bàn đích giữ trạng thái OCCUPIED, có thể tính lại tổng tiền hóa đơn nếu muốn
    }

    /**
     * Tách bàn: chuyển một phần món từ bàn nguồn sang bàn đích
     */
    @Override
    @Transactional
    public void splitTable(SplitTableRequest request, Integer employeeId) {
        // Lấy id bàn nguồn và bàn đích từ request
        Integer sourceTableId = request.getSourceTableId();
        Integer targetTableId = request.getTargetTableId();

        // Nếu bàn nguồn và bàn đích trùng nhau thì báo lỗi
        if (sourceTableId.equals(targetTableId)) {
            throw new IllegalArgumentException("Bàn nguồn và bàn đích không được trùng nhau");
        }

        // Lấy thông tin bàn nguồn và bàn đích từ database
        Optional<TableEntity> sourceTableOpt = tableRepository.findById(sourceTableId);
        Optional<TableEntity> targetTableOpt = tableRepository.findById(targetTableId);

        if (sourceTableOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy bàn nguồn");
        }
        if (targetTableOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy bàn đích");
        }

        TableEntity sourceTable = sourceTableOpt.get();
        TableEntity targetTable = targetTableOpt.get();

        // Kiểm tra trạng thái bàn nguồn và bàn đích
        if (sourceTable.getStatus() != TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Chỉ có thể tách từ bàn đang sử dụng (OCCUPIED)");
        }
        if (targetTable.getStatus() != TableStatus.AVAILABLE && targetTable.getStatus() != TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Bàn đích phải là bàn trống (AVAILABLE) hoặc đang sử dụng (OCCUPIED)");
        }

        // Lấy reservation và invoice của bàn nguồn
        ReservationEntity sourceReservation = reservationRepository.findCurrentReservationByTableId(sourceTableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy reservation của bàn nguồn"));
        InvoiceEntity sourceInvoice = sourceReservation.getInvoice();
        if (sourceInvoice == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn của bàn nguồn");
        }

        // Đánh dấu hóa đơn nguồn là UNDER_REVIEW
        sourceInvoice.setStatus(InvoiceStatus.UNDER_REVIEW);
        invoiceRepository.save(sourceInvoice);

        // Lấy các chi tiết hóa đơn của bàn nguồn
        List<InvoiceDetailEntity> sourceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(sourceInvoice.getId());
        Map<Integer, InvoiceDetailEntity> sourceDetailMap = new HashMap<>();
        for (InvoiceDetailEntity detail : sourceDetails) {
            sourceDetailMap.put(detail.getMenuItem().getId(), detail);
        }

        // Kiểm tra số lượng món cần tách có hợp lệ không
        for (SplitTableRequest.SplitItemRequest item : request.getItems()) {
            Integer menuItemId = item.getMenuItemId();
            Integer splitQuantity = item.getQuantity();

            InvoiceDetailEntity sourceDetail = sourceDetailMap.get(menuItemId);
            if (sourceDetail == null) {
                throw new IllegalArgumentException("Món với ID " + menuItemId + " không có trong bàn nguồn");
            }
            if (sourceDetail.getQuantity() < splitQuantity) {
                throw new IllegalArgumentException("Số lượng tách món " + menuItemId + " vượt quá số lượng hiện có");
            }
        }

        // Khai báo các biến cho hóa đơn và reservation bàn đích
        InvoiceEntity targetInvoice;
        ReservationEntity targetReservation;
        Map<Integer, InvoiceDetailEntity> targetDetailMap = new HashMap<>();

        // Nếu bàn đích đang trống thì tạo mới hóa đơn và reservation cho bàn đích
        if (targetTable.getStatus() == TableStatus.AVAILABLE) {
            EmployeeEntity employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

            targetInvoice = new InvoiceEntity();
            targetInvoice.setTotalAmount(0.0);
            targetInvoice.setCreatedAt(LocalDateTime.now());
            targetInvoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
            targetInvoice.setIsDeleted(false);
            targetInvoice = invoiceRepository.save(targetInvoice);

            targetReservation = new ReservationEntity();
            ReservationKey reservationKey = new ReservationKey();
            reservationKey.setIdTable(targetTable.getId());
            reservationKey.setIdEmployee(employee.getId());
            reservationKey.setIdInvoice(targetInvoice.getId());

            targetReservation.setId(reservationKey);
            targetReservation.setTable(targetTable);
            targetReservation.setEmployee(employee);
            targetReservation.setCustomerName(sourceReservation.getCustomerName());
            targetReservation.setCustomerPhone(sourceReservation.getCustomerPhone());
            targetReservation.setReservationDate(LocalDateTime.now());
            targetReservation.setInvoice(targetInvoice);
            targetReservation.setIsDeleted(false);
            reservationRepository.save(targetReservation);

            targetTable.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(targetTable);

        } else {
            // Nếu bàn đích đã có reservation thì lấy hóa đơn và các chi tiết hóa đơn của bàn đích
            targetReservation = reservationRepository.findCurrentReservationByTableId(targetTableId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy reservation của bàn đích"));
            targetInvoice = targetReservation.getInvoice();
            if (targetInvoice == null) {
                throw new IllegalArgumentException("Không tìm thấy hóa đơn của bàn đích");
            }

            List<InvoiceDetailEntity> targetDetails = invoiceDetailRepository
                    .findAllByInvoice_IdAndIsDeletedFalse(targetInvoice.getId());
            for (InvoiceDetailEntity detail : targetDetails) {
                targetDetailMap.put(detail.getMenuItem().getId(), detail);
            }
        }

        // Thực hiện chuyển món từ bàn nguồn sang bàn đích
        for (SplitTableRequest.SplitItemRequest item : request.getItems()) {
            Integer menuItemId = item.getMenuItemId();
            Integer splitQuantity = item.getQuantity();

            InvoiceDetailEntity sourceDetail = sourceDetailMap.get(menuItemId);
            MenuItemEntity menuItem = sourceDetail.getMenuItem();

            InvoiceDetailEntity targetDetail = targetDetailMap.get(menuItemId);
            // Nếu bàn đích đã có món này thì cộng dồn số lượng
            if (targetDetail != null) {
                targetDetail.setQuantity(targetDetail.getQuantity() + splitQuantity);
                invoiceDetailRepository.save(targetDetail);
            } else {
                // Nếu chưa có thì tạo mới một chi tiết hóa đơn cho bàn đích
                InvoiceDetailEntity newDetail = new InvoiceDetailEntity();
                com.viettridao.cafe.model.InvoiceKey newKey = new com.viettridao.cafe.model.InvoiceKey();
                newKey.setIdInvoice(targetInvoice.getId());
                newKey.setIdMenuItem(menuItemId);
                newDetail.setId(newKey);
                newDetail.setInvoice(targetInvoice);
                newDetail.setMenuItem(menuItem);
                newDetail.setQuantity(splitQuantity);
                newDetail.setPrice(sourceDetail.getPrice());
                newDetail.setIsDeleted(false);
                invoiceDetailRepository.save(newDetail);
                targetDetailMap.put(menuItemId, newDetail);
            }

            // Cập nhật lại số lượng món ở bàn nguồn, nếu hết thì xóa mềm chi tiết hóa đơn
            int remainingQuantity = sourceDetail.getQuantity() - splitQuantity;
            if (remainingQuantity > 0) {
                sourceDetail.setQuantity(remainingQuantity);
                invoiceDetailRepository.save(sourceDetail);
            } else {
                sourceDetail.setIsDeleted(true);
                invoiceDetailRepository.save(sourceDetail);
            }
        }

        // Kiểm tra nếu bàn nguồn hết món thì hủy hóa đơn, reservation và cập nhật trạng thái bàn
        List<InvoiceDetailEntity> remainingSourceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(sourceInvoice.getId());

        if (remainingSourceDetails.isEmpty()) {
            sourceInvoice.setStatus(InvoiceStatus.CANCELLED);
            sourceInvoice.setIsDeleted(true);
            invoiceRepository.save(sourceInvoice);

            sourceReservation.setIsDeleted(true);
            reservationRepository.save(sourceReservation);

            sourceTable.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(sourceTable);
        } else {
            // Nếu còn món thì chuyển trạng thái hóa đơn thành PENDING_PAYMENT
            sourceInvoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
            invoiceRepository.save(sourceInvoice);
        }
    }

    @Override
    public Map<String, Object> prepareSplitTableForm(Integer sourceTableId) {
        Map<String, Object> result = new HashMap<>();

        // 1. Lấy thông tin bàn nguồn
        TableEntity sourceTable = tableRepository.findById(sourceTableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn nguồn với ID: " + sourceTableId));
        result.put("sourceTable", sourceTable);

        // 2. Kiểm tra trạng thái bàn nguồn
        if (sourceTable.getStatus() != TableStatus.OCCUPIED) {
            throw new IllegalArgumentException(
                    "Chỉ có thể tách từ bàn đang sử dụng (OCCUPIED). Bàn hiện tại: " + sourceTable.getStatus()
            );
        }

        // 3. Tìm reservation của bàn nguồn
        ReservationEntity sourceReservation = reservationRepository.findCurrentReservationByTableId(sourceTableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin đặt bàn cho bàn nguồn"));
        result.put("sourceReservation", sourceReservation);

        // 4. Kiểm tra hóa đơn của bàn nguồn
        InvoiceEntity sourceInvoice = sourceReservation.getInvoice();
        if (sourceInvoice == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn cho bàn nguồn");
        }

        // 5. Lấy danh sách món trong hóa đơn
        List<InvoiceDetailEntity> invoiceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(sourceInvoice.getId());
        if (invoiceDetails.isEmpty()) {
            throw new IllegalArgumentException("Bàn nguồn không có món nào để tách");
        }
        result.put("sourceInvoiceDetails", invoiceDetails);

        // 6. Lấy danh sách bàn khả dụng để tách đến
        List<TableEntity> allTables = tableRepository.findAll();
        List<TableEntity> availableTables = allTables.stream()
                .filter(table -> table.getStatus() == TableStatus.AVAILABLE)
                .toList();
        List<TableEntity> occupiedTables = allTables.stream()
                .filter(table -> table.getStatus() == TableStatus.OCCUPIED && !table.getId().equals(sourceTableId))
                .toList();

        result.put("availableTables", availableTables);
        result.put("occupiedTables", occupiedTables);

        if (availableTables.isEmpty() && occupiedTables.isEmpty()) {
            throw new IllegalArgumentException("Không có bàn nào khả dụng để tách đến");
        }

        // 7. Chuẩn bị request tách bàn mẫu cho form
        SplitTableRequest splitRequest = new SplitTableRequest();
        splitRequest.setSourceTableId(sourceTableId);
        List<SplitTableRequest.SplitItemRequest> items = new ArrayList<>();
        for (InvoiceDetailEntity detail : invoiceDetails) {
            SplitTableRequest.SplitItemRequest item = new SplitTableRequest.SplitItemRequest();
            item.setMenuItemId(detail.getMenuItem().getId());
            item.setQuantity(0); // mặc định 0, user nhập sau
            items.add(item);
        }
        splitRequest.setItems(items);

        result.put("splitTableRequest", splitRequest);
        result.put("selectedTableId", sourceTableId);

        // Có thể thêm các trường khác nếu view cần
        return result;
    }

    /**
     * Chuyển bàn: chuyển toàn bộ món từ bàn nguồn sang bàn đích
     */
    @Override
    @Transactional
    public void moveTable(MoveTableRequest request, Integer employeeId) {
        // Lấy id bàn nguồn và bàn đích từ request
        Integer sourceTableId = request.getSourceTableId();
        Integer targetTableId = request.getTargetTableId();

        // Nếu bàn nguồn và bàn đích trùng nhau thì báo lỗi
        if (sourceTableId.equals(targetTableId)) {
            throw new IllegalArgumentException("Bàn nguồn và bàn đích không được trùng nhau");
        }

        // Lấy thông tin bàn nguồn và bàn đích từ database
        TableEntity sourceTable = tableRepository.findById(sourceTableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn nguồn"));
        TableEntity targetTable = tableRepository.findById(targetTableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn đích"));

        // Kiểm tra trạng thái bàn nguồn và bàn đích
        if (sourceTable.getStatus() != TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Chỉ có thể chuyển từ bàn đang sử dụng (OCCUPIED)");
        }
        if (targetTable.getStatus() != TableStatus.AVAILABLE) {
            throw new IllegalArgumentException("Chỉ có thể chuyển sang bàn trống (AVAILABLE)");
        }

        // Lấy reservation và invoice của bàn nguồn
        ReservationEntity sourceReservation = reservationRepository.findCurrentReservationByTableId(sourceTableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy reservation của bàn nguồn"));
        InvoiceEntity sourceInvoice = sourceReservation.getInvoice();
        if (sourceInvoice == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn của bàn nguồn");
        }

        // Kiểm tra nhân viên thực hiện chuyển bàn có tồn tại không
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên thực hiện chuyển bàn"));

        // Tạo mới ReservationKey cho bàn đích
        ReservationKey newKey = new ReservationKey();
        newKey.setIdTable(targetTable.getId());
        newKey.setIdEmployee(employee.getId());
        newKey.setIdInvoice(sourceInvoice.getId());

        // Tạo mới reservation cho bàn đích
        ReservationEntity targetReservation = new ReservationEntity();
        targetReservation.setId(newKey);
        targetReservation.setTable(targetTable);
        targetReservation.setEmployee(employee);
        targetReservation.setCustomerName(sourceReservation.getCustomerName());
        targetReservation.setCustomerPhone(sourceReservation.getCustomerPhone());
        targetReservation.setReservationDate(sourceReservation.getReservationDate());
        targetReservation.setInvoice(sourceInvoice);
        targetReservation.setIsDeleted(false);
        reservationRepository.save(targetReservation);

        // Cập nhật trạng thái hóa đơn, bàn đích và bàn nguồn
        sourceInvoice.setIsDeleted(false);
        invoiceRepository.save(sourceInvoice);

        targetTable.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(targetTable);

        sourceTable.setStatus(TableStatus.AVAILABLE);
        tableRepository.save(sourceTable);

        // Đánh dấu reservation bàn nguồn là đã xóa mềm
        sourceReservation.setIsDeleted(true);
        reservationRepository.save(sourceReservation);
        // Không cần xóa mềm invoice hay invoice detail vì đã chuyển toàn bộ sang bàn mới
    }

    /**
     * Chuẩn bị dữ liệu cho form chuyển bàn: kiểm tra và trả về các thông tin cần thiết.
     *
     * @param sourceTableId ID bàn nguồn
     * @return Map chứa dữ liệu cho form (sourceTable, availableTables, tất cả tables,...)
     * @throws IllegalArgumentException nếu gặp lỗi nghiệp vụ
     */
    @Override
    public Map<String, Object> prepareMoveTableForm(Integer sourceTableId) {
        Map<String, Object> result = new HashMap<>();
        // 1. Lấy thông tin bàn nguồn
        TableEntity sourceTable = tableRepository.findById(sourceTableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn nguồn với ID: " + sourceTableId));
        // 2. Kiểm tra bàn nguồn phải OCCUPIED
        if (sourceTable.getStatus() != TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Chỉ có thể chuyển từ bàn đang sử dụng (OCCUPIED). Bàn hiện tại: " + sourceTable.getStatus());
        }
        // 3. Tìm reservation bàn nguồn
        ReservationEntity sourceReservation = findCurrentReservationByTableId(sourceTableId);
        if (sourceReservation == null) {
            throw new IllegalArgumentException("Không tìm thấy thông tin đặt bàn cho bàn nguồn");
        }
        // 4. Lấy danh sách bàn
        List<TableEntity> allTables = tableRepository.findAll();
        List<TableEntity> availableTables = allTables.stream().filter(table -> table.getStatus() == TableStatus.AVAILABLE).toList();
        if (availableTables.isEmpty()) {
            throw new IllegalArgumentException("Không có bàn trống nào để chuyển đến");
        }
        // 5. Trả dữ liệu
        result.put("sourceTable", sourceTable);
        result.put("tables", allTables);
        result.put("availableTables", availableTables);
        return result;
    }
}