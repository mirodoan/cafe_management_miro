package com.viettridao.cafe.service.impl;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final InvoiceRepository invoiceRepository;
    private final EmployeeRepository employeeRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final OrderDetailMapper orderDetailMapper;

    // ======================= Private helper methods ===========================

    /**
     * Lấy bàn theo ID và kiểm tra trạng thái nếu cần.
     *
     * @param tableId
     * @param requiredStatus Trạng thái bàn mong muốn (nullable)
     * @return TableEntity
     * @throws IllegalArgumentException nếu không tìm thấy bàn hoặc trạng thái không đúng
     */
    private TableEntity requireTable(Integer tableId, TableStatus requiredStatus) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với ID: " + tableId));
        if (requiredStatus != null && table.getStatus() != requiredStatus) {
            throw new IllegalArgumentException("Bàn không ở trạng thái " + requiredStatus);
        }
        return table;
    }

    /**
     * Lấy reservation hiện tại theo tableId, ném lỗi nếu không có.
     */
    private ReservationEntity requireCurrentReservation(Integer tableId) {
        return reservationRepository.findCurrentReservationByTableId(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy reservation cho bàn " + tableId));
    }

    /**
     * Lấy invoice từ reservation, ném lỗi nếu không có.
     */
    private InvoiceEntity requireInvoice(ReservationEntity reservation) {
        InvoiceEntity invoice = reservation.getInvoice();
        if (invoice == null)
            throw new IllegalArgumentException("Không tìm thấy hóa đơn");
        return invoice;
    }

    /**
     * Lấy nhân viên theo Id, ném lỗi nếu không có.
     */
    private EmployeeEntity requireEmployee(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
    }

    /**
     * Cập nhật trạng thái bàn và lưu lại.
     */
    private void updateTableStatus(TableEntity table, TableStatus status) {
        table.setStatus(status);
        tableRepository.save(table);
    }

    /**
     * Cập nhật trạng thái hóa đơn và lưu lại.
     */
    private void updateInvoiceStatus(InvoiceEntity invoice, InvoiceStatus status) {
        invoice.setStatus(status);
        invoiceRepository.save(invoice);
    }

    /**
     * Đánh dấu reservation là đã xóa mềm.
     */
    private void softDeleteReservation(ReservationEntity reservation) {
        reservation.setIsDeleted(true);
        reservationRepository.save(reservation);
    }

    /**
     * Đánh dấu invoice là đã xóa mềm và cập nhật trạng thái.
     */
    private void softDeleteInvoice(InvoiceEntity invoice, InvoiceStatus status) {
        invoice.setStatus(status);
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
    }

    /**
     * Kiểm tra giờ đặt bàn hợp lệ (từ 8h đến trước 21h).
     */
    private boolean isValidReservationTime(LocalDateTime reservationDate) {
        int hour = reservationDate.getHour();
        return hour >= 8 && hour < 21;
    }

    // ======================= Service methods ===========================

    /**
     * Tìm reservation hiện tại theo tableId, trả về null nếu không có.
     */
    @Override
    public ReservationEntity findCurrentReservationByTableId(Integer tableId) {
        return reservationRepository.findCurrentReservationByTableId(tableId).orElse(null);
    }

    /**
     * Lưu reservation, invoice và table liên quan nếu không null (transactional).
     */
    @Override
    @Transactional
    public void saveReservationAndRelated(ReservationEntity reservation,
                                          InvoiceEntity invoice, TableEntity table) {
        if (reservation == null) throw new IllegalArgumentException("Reservation không được null!");
        reservationRepository.save(reservation);
        if (invoice != null) invoiceRepository.save(invoice);
        if (table != null) tableRepository.save(table);
    }

    /**
     * Lấy chi tiết order của bàn theo tableId.
     * Trả về null nếu không có reservation hoặc invoice hợp lệ.
     */
    @Override
    public OrderDetailRessponse getOrderDetailByTableId(Integer tableId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với ID: " + tableId));

        ReservationEntity reservation = reservationRepository.findCurrentReservationByTableId(tableId)
                .orElse(null);

        // Nếu không có reservation hoặc đã xóa mềm thì coi như bàn trống
        if (reservation == null || Boolean.TRUE.equals(reservation.getIsDeleted()))
            return null;

        InvoiceEntity invoice = reservation.getInvoice();
        if (invoice == null || Boolean.TRUE.equals(invoice.getIsDeleted()))
            return null;

        // Chỉ lấy các món còn quantity > 0 và chưa xóa mềm
        List<InvoiceDetailEntity> invoiceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(invoice.getId())
                .stream().filter(d -> d.getQuantity() > 0).toList();

        // Nếu không còn món nào, trả về null hoặc empty, hoặc có thể soft-delete reservation/invoice tại đây luôn (phòng trường hợp logic trước đó bị lỗi)
        return orderDetailMapper.toOrderDetailResponse(
                table, invoice, reservation, invoiceDetails.isEmpty() ? new ArrayList<>() : invoiceDetails
        );
    }

    /**
     * Lấy thông tin thanh toán cho bàn OCCUPIED.
     */
    @Override
    public OrderDetailRessponse getPaymentInfoForTable(Integer tableId) {
        TableEntity table = requireTable(tableId, TableStatus.OCCUPIED);
        ReservationEntity reservation = requireCurrentReservation(tableId);
        InvoiceEntity invoice = requireInvoice(reservation);
        List<InvoiceDetailEntity> invoiceDetails =
                invoiceDetailRepository.findAllByInvoice_IdAndIsDeletedFalse(invoice.getId())
                        .stream().filter(d -> d.getQuantity() > 0).toList();
        return orderDetailMapper.toOrderDetailResponse(table, invoice, reservation, invoiceDetails);
    }

    /**
     * Xử lý thanh toán hóa đơn cho bàn, cập nhật trạng thái liên quan (transactional).
     */
    @Override
    @Transactional
    public void payInvoiceForTable(Integer tableId) {
        TableEntity table = requireTable(tableId, null);
        ReservationEntity reservation = requireCurrentReservation(tableId);
        InvoiceEntity invoice = requireInvoice(reservation);
        // Đánh dấu hóa đơn đã thanh toán, soft-delete reservation, trả bàn về AVAILABLE
        updateInvoiceStatus(invoice, InvoiceStatus.PAID);
        softDeleteReservation(reservation);
        updateTableStatus(table, TableStatus.AVAILABLE);
        saveReservationAndRelated(reservation, invoice, table);
    }

    /**
     * Tạo mới reservation cho bàn (transactional), kiểm tra giờ đặt và trạng thái bàn.
     */
    @Override
    @Transactional
    public ReservationEntity createReservation(CreateReservationRequest request, Integer employeeId) {
        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.of(21, 0)) || now.equals(LocalTime.of(21, 0)))
            throw new IllegalArgumentException("Quán sắp đóng cửa, không nhận đặt bàn sau 21h. Vui lòng quay lại vào hôm sau!");

        if (!isValidReservationTime(request.getReservationDateTime()))
            throw new IllegalArgumentException("Chỉ được đặt bàn từ 8:00 đến trước 21:00 trong ngày.");

        TableEntity table = requireTable(request.getTableId(), TableStatus.AVAILABLE);
        EmployeeEntity employee = requireEmployee(employeeId);

        // Tạo mới invoice
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setStatus(InvoiceStatus.RESERVED);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setIsDeleted(false);
        invoiceRepository.save(invoice);

        // Tạo mới reservation
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

        reservationRepository.save(reservation);
        updateTableStatus(table, TableStatus.RESERVED);
        return reservation;
    }

    /**
     * Hủy đặt bàn: soft-delete reservation và invoice, cập nhật lại trạng thái bàn (transactional).
     */
    @Override
    @Transactional
    public void cancelReservation(Integer tableId) {
        ReservationEntity reservation = requireCurrentReservation(tableId);
        if (Boolean.TRUE.equals(reservation.getIsDeleted()))
            throw new IllegalArgumentException("Không tìm thấy thông tin đặt bàn để hủy!");
        if (reservation.getTable().getStatus() != TableStatus.RESERVED)
            throw new IllegalArgumentException("Chỉ có thể hủy bàn ở trạng thái ĐÃ ĐẶT!");

        softDeleteReservation(reservation);
        InvoiceEntity invoice = reservation.getInvoice();
        if (invoice != null) softDeleteInvoice(invoice, InvoiceStatus.CANCELLED);
        updateTableStatus(reservation.getTable(), TableStatus.AVAILABLE);
        saveReservationAndRelated(reservation, invoice, reservation.getTable());
    }

    /**
     * Gộp nhiều bàn đang sử dụng về 1 bàn đích, cộng dồn các món, soft-delete reservation/invoice nguồn (transactional).
     */
    @Override
    @Transactional
    public void mergeTables(MergeTableRequest request, Integer employeeId) {
        List<Integer> tableIds = request.getTableIds();
        Integer targetTableId = request.getTargetTableId();
        if (tableIds == null || tableIds.size() < 2)
            throw new IllegalArgumentException("Phải chọn ít nhất 2 bàn để gộp");
        if (!tableIds.contains(targetTableId))
            throw new IllegalArgumentException("Bàn gộp đến phải nằm trong danh sách bàn đã chọn");

        // Lấy danh sách bàn gộp và kiểm tra trạng thái
        List<TableEntity> tables = tableRepository.findAllById(tableIds);
        if (tables.size() != tableIds.size())
            throw new IllegalArgumentException("Có bàn không tồn tại");
        for (TableEntity t : tables)
            if (t.getStatus() != TableStatus.OCCUPIED)
                throw new IllegalArgumentException("Chỉ được gộp các bàn đang sử dụng (OCCUPIED)");

        TableEntity targetTable = tables.stream().filter(t -> t.getId().equals(targetTableId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn gộp đến"));
        ReservationEntity targetReservation = requireCurrentReservation(targetTableId);
        InvoiceEntity targetInvoice = requireInvoice(targetReservation);

        // Map menuItemId -> InvoiceDetailEntity cho bàn đích
        Map<Integer, InvoiceDetailEntity> targetDetailMap = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(targetInvoice.getId())
                .stream().collect(Collectors.toMap(d -> d.getMenuItem().getId(), d -> d));

        // Duyệt từng bàn nguồn, gộp các món sang bàn đích
        for (TableEntity srcTable : tables) {
            if (srcTable.getId().equals(targetTableId)) continue;
            ReservationEntity srcReservation = reservationRepository.findCurrentReservationByTableId(srcTable.getId()).orElse(null);
            if (srcReservation == null) continue;
            InvoiceEntity srcInvoice = srcReservation.getInvoice();
            if (srcInvoice == null) continue;

            updateInvoiceStatus(srcInvoice, InvoiceStatus.UNDER_REVIEW);

            List<InvoiceDetailEntity> srcDetails = invoiceDetailRepository
                    .findAllByInvoice_IdAndIsDeletedFalse(srcInvoice.getId());
            for (InvoiceDetailEntity srcDetail : srcDetails) {
                Integer menuItemId = srcDetail.getMenuItem().getId();
                InvoiceDetailEntity targetDetail = targetDetailMap.get(menuItemId);
                if (targetDetail != null) {
                    targetDetail.setQuantity(targetDetail.getQuantity() + srcDetail.getQuantity());
                    invoiceDetailRepository.save(targetDetail);
                } else {
                    InvoiceDetailEntity newDetail = new InvoiceDetailEntity();
                    InvoiceKey newKey = new InvoiceKey();
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
                srcDetail.setIsDeleted(true);
                invoiceDetailRepository.save(srcDetail);
            }
            // Soft-delete invoice và reservation nguồn, cập nhật lại trạng thái bàn
            softDeleteInvoice(srcInvoice, InvoiceStatus.CANCELLED);
            softDeleteReservation(srcReservation);
            updateTableStatus(srcTable, TableStatus.AVAILABLE);
        }
    }

    /**
     * Tách món từ bàn nguồn sang bàn đích (transactional).
     * Bàn đích có thể là bàn mới (AVAILABLE) hoặc đã có khách (OCCUPIED).
     */
    @Override
    @Transactional
    public void splitTable(SplitTableRequest request, Integer employeeId) {
        Integer sourceTableId = request.getSourceTableId();
        Integer targetTableId = request.getTargetTableId();
        if (sourceTableId.equals(targetTableId))
            throw new IllegalArgumentException("Bàn nguồn và bàn đích không được trùng nhau");

        TableEntity sourceTable = requireTable(sourceTableId, TableStatus.OCCUPIED);
        TableEntity targetTable = requireTable(targetTableId, null);
        if (targetTable.getStatus() != TableStatus.AVAILABLE && targetTable.getStatus() != TableStatus.OCCUPIED)
            throw new IllegalArgumentException("Bàn đích phải là bàn trống (AVAILABLE) hoặc đang sử dụng (OCCUPIED)");

        ReservationEntity sourceReservation = requireCurrentReservation(sourceTableId);
        InvoiceEntity sourceInvoice = requireInvoice(sourceReservation);

        updateInvoiceStatus(sourceInvoice, InvoiceStatus.UNDER_REVIEW);

        // Map các món còn lại ở bàn nguồn
        Map<Integer, InvoiceDetailEntity> sourceDetailMap = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(sourceInvoice.getId())
                .stream().collect(Collectors.toMap(d -> d.getMenuItem().getId(), d -> d));

        // Validate số lượng món tách
        for (SplitTableRequest.SplitItemRequest item : request.getItems()) {
            Integer menuItemId = item.getMenuItemId();
            Integer splitQuantity = item.getQuantity();
            if (splitQuantity == null || splitQuantity <= 0) continue;
            InvoiceDetailEntity sourceDetail = sourceDetailMap.get(menuItemId);
            if (sourceDetail == null)
                throw new IllegalArgumentException("Món với ID " + menuItemId + " không có trong bàn nguồn");
            if (sourceDetail.getQuantity() < splitQuantity)
                throw new IllegalArgumentException("Số lượng tách món " + menuItemId + " vượt quá số lượng hiện có");
        }

        InvoiceEntity targetInvoice;
        ReservationEntity targetReservation;
        Map<Integer, InvoiceDetailEntity> targetDetailMap = new HashMap<>();

        // Nếu bàn đích là AVAILABLE, tạo mới invoice & reservation cho bàn đích
        if (targetTable.getStatus() == TableStatus.AVAILABLE) {
            EmployeeEntity employee = requireEmployee(employeeId);

            targetInvoice = new InvoiceEntity();
            targetInvoice.setTotalAmount(0.0);
            targetInvoice.setCreatedAt(LocalDateTime.now());
            targetInvoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
            targetInvoice.setIsDeleted(false);
            invoiceRepository.save(targetInvoice);

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

            updateTableStatus(targetTable, TableStatus.OCCUPIED);

        } else {
            // Nếu bàn đích đã có khách, lấy reservation/invoice hiện tại
            targetReservation = requireCurrentReservation(targetTableId);
            targetInvoice = requireInvoice(targetReservation);
            targetDetailMap = invoiceDetailRepository
                    .findAllByInvoice_IdAndIsDeletedFalse(targetInvoice.getId())
                    .stream().collect(Collectors.toMap(d -> d.getMenuItem().getId(), d -> d));
        }

        // Tách từng món sang bàn đích
        for (SplitTableRequest.SplitItemRequest item : request.getItems()) {
            Integer menuItemId = item.getMenuItemId();
            Integer splitQuantity = item.getQuantity();
            if (splitQuantity == null || splitQuantity <= 0) continue;
            InvoiceDetailEntity sourceDetail = sourceDetailMap.get(menuItemId);
            MenuItemEntity menuItem = sourceDetail.getMenuItem();
            InvoiceDetailEntity targetDetail = targetDetailMap.get(menuItemId);

            if (targetDetail != null) {
                targetDetail.setQuantity(targetDetail.getQuantity() + splitQuantity);
                invoiceDetailRepository.save(targetDetail);
            } else {
                InvoiceDetailEntity newDetail = new InvoiceDetailEntity();
                InvoiceKey newKey = new InvoiceKey();
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

            int remainingQuantity = sourceDetail.getQuantity() - splitQuantity;
            if (remainingQuantity > 0) {
                sourceDetail.setQuantity(remainingQuantity);
                invoiceDetailRepository.save(sourceDetail);
            } else {
                sourceDetail.setIsDeleted(true);
                invoiceDetailRepository.save(sourceDetail);
            }
        }

        // Nếu bàn nguồn hết món thì xóa mềm reservation/invoice, trả bàn về AVAILABLE
        List<InvoiceDetailEntity> remainingSourceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(sourceInvoice.getId());

        if (remainingSourceDetails.isEmpty()) {
            softDeleteInvoice(sourceInvoice, InvoiceStatus.CANCELLED);
            softDeleteReservation(sourceReservation);
            updateTableStatus(sourceTable, TableStatus.AVAILABLE);
        } else {
            updateInvoiceStatus(sourceInvoice, InvoiceStatus.PENDING_PAYMENT);
        }
    }

    /**
     * Chuẩn bị dữ liệu cho form tách bàn (danh sách bàn, món, request mẫu...).
     */
    @Override
    public Map<String, Object> prepareSplitTableForm(Integer sourceTableId) {
        Map<String, Object> result = new HashMap<>();
        TableEntity sourceTable = requireTable(sourceTableId, TableStatus.OCCUPIED);
        result.put("sourceTable", sourceTable);

        ReservationEntity sourceReservation = requireCurrentReservation(sourceTableId);
        result.put("sourceReservation", sourceReservation);

        InvoiceEntity sourceInvoice = requireInvoice(sourceReservation);

        // Lấy danh sách món của bàn nguồn
        List<InvoiceDetailEntity> invoiceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(sourceInvoice.getId());
        if (invoiceDetails.isEmpty())
            throw new IllegalArgumentException("Bàn nguồn không có món nào để tách");
        result.put("sourceInvoiceDetails", invoiceDetails);

        // Lấy danh sách bàn trống và bàn đang sử dụng khác
        List<TableEntity> allTables = tableRepository.findAll();
        List<TableEntity> availableTables = allTables.stream()
                .filter(table -> table.getStatus() == TableStatus.AVAILABLE)
                .toList();
        List<TableEntity> occupiedTables = allTables.stream()
                .filter(table -> table.getStatus() == TableStatus.OCCUPIED && !table.getId().equals(sourceTableId))
                .toList();

        result.put("availableTables", availableTables);
        result.put("occupiedTables", occupiedTables);

        if (availableTables.isEmpty() && occupiedTables.isEmpty())
            throw new IllegalArgumentException("Không có bàn nào khả dụng để tách đến");

        // Tạo request mẫu cho form tách bàn
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
        return result;
    }

    /**
     * Chuyển toàn bộ reservation/invoice từ bàn nguồn sang bàn đích (transactional).
     */
    @Override
    @Transactional
    public void moveTable(MoveTableRequest request, Integer employeeId) {
        Integer sourceTableId = request.getSourceTableId();
        Integer targetTableId = request.getTargetTableId();
        if (sourceTableId.equals(targetTableId))
            throw new IllegalArgumentException("Bàn nguồn và bàn đích không được trùng nhau");

        TableEntity sourceTable = requireTable(sourceTableId, TableStatus.OCCUPIED);
        TableEntity targetTable = requireTable(targetTableId, TableStatus.AVAILABLE);

        ReservationEntity sourceReservation = requireCurrentReservation(sourceTableId);
        InvoiceEntity sourceInvoice = requireInvoice(sourceReservation);
        EmployeeEntity employee = requireEmployee(employeeId);

        // Tạo reservation mới cho bàn đích, gán lại invoice cũ
        ReservationKey newKey = new ReservationKey();
        newKey.setIdTable(targetTable.getId());
        newKey.setIdEmployee(employee.getId());
        newKey.setIdInvoice(sourceInvoice.getId());

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

        sourceInvoice.setIsDeleted(false);
        invoiceRepository.save(sourceInvoice);

        updateTableStatus(targetTable, TableStatus.OCCUPIED);
        updateTableStatus(sourceTable, TableStatus.AVAILABLE);

        softDeleteReservation(sourceReservation);
    }

    /**
     * Chuẩn bị dữ liệu cho form chuyển bàn (danh sách bàn, bàn nguồn, bàn trống...).
     */
    @Override
    public Map<String, Object> prepareMoveTableForm(Integer sourceTableId) {
        Map<String, Object> result = new HashMap<>();
        TableEntity sourceTable = requireTable(sourceTableId, TableStatus.OCCUPIED);
        ReservationEntity sourceReservation = findCurrentReservationByTableId(sourceTableId);
        if (sourceReservation == null)
            throw new IllegalArgumentException("Không tìm thấy thông tin đặt bàn cho bàn nguồn");

        // Lấy danh sách bàn và bàn trống
        List<TableEntity> allTables = tableRepository.findAll();
        List<TableEntity> availableTables = allTables.stream()
                .filter(table -> table.getStatus() == TableStatus.AVAILABLE)
                .toList();
        if (availableTables.isEmpty())
            throw new IllegalArgumentException("Không có bàn trống nào để chuyển đến");

        result.put("sourceTable", sourceTable);
        result.put("tables", allTables);
        result.put("availableTables", availableTables);
        return result;
    }
}