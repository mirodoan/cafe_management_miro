package com.viettridao.cafe.service.impl;

// Các import thư viện, entity, repository, service cần thiết

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.sales.CreateReservationRequest;
import com.viettridao.cafe.dto.request.sales.MergeTableRequest;
import com.viettridao.cafe.dto.request.sales.MoveTableRequest;
import com.viettridao.cafe.dto.request.sales.SplitTableRequest;
import com.viettridao.cafe.model.*;
import com.viettridao.cafe.repository.*;
import com.viettridao.cafe.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Tạo mới một đặt bàn.
     *
     * @param request    Đối tượng chứa thông tin cần thiết để tạo đặt bàn mới.
     * @param employeeId ID của nhân viên thực hiện đặt bàn.
     * @return Thực thể ReservationEntity vừa được tạo.
     */
    @Override
    @Transactional
    public ReservationEntity createReservation(CreateReservationRequest request, Integer employeeId) {
        // Kiểm tra trạng thái bàn có khả dụng không
        Optional<TableEntity> tableOpt = tableRepository.findById(request.getTableId());
        if (tableOpt.isEmpty() || tableOpt.get().getStatus() != TableStatus.AVAILABLE) {
            throw new IllegalArgumentException("Bàn không tồn tại hoặc không khả dụng.");
        }

        TableEntity table = tableOpt.get();

        // Kiểm tra nhân viên thực hiện đặt bàn có tồn tại không
        Optional<EmployeeEntity> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Nhân viên không tồn tại.");
        }

        EmployeeEntity employee = employeeOpt.get();

        // Tạo mới hóa đơn cho đặt bàn
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setStatus(InvoiceStatus.RESERVED);
        invoice.setCreatedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        // Tạo mới thông tin đặt bàn (reservation)
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
        reservation.setReservationDate(request.getReservationDate());
        reservation.setInvoice(invoice);
        reservation.setIsDeleted(false);

        // Lưu thông tin đặt bàn vào database
        reservationRepository.save(reservation);

        // Cập nhật trạng thái bàn thành RESERVED
        table.setStatus(TableStatus.RESERVED);
        tableRepository.save(table);

        return reservation;
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
            // Bỏ qua bàn đích
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
}