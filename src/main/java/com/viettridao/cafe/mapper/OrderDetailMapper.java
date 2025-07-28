package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.sales.OrderDetailRessponse;
import com.viettridao.cafe.model.InvoiceDetailEntity;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.TableEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderDetailMapper
 * Mapper chuyển đổi entity sang OrderDetailRessponse cho màn hình xem bàn, thanh toán, chọn món.
 */
@Component
public class OrderDetailMapper {
    /**
     * Mapping entity sang OrderDetailRessponse (response chi tiết order/bàn).
     * - Gồm thông tin bàn, hóa đơn, đặt bàn, danh sách món và tổng tiền.
     *
     * @param table          Bàn
     * @param invoice        Hóa đơn (nếu có)
     * @param reservation    Đặt bàn (nếu có)
     * @param invoiceDetails Danh sách chi tiết hóa đơn
     * @return OrderDetailRessponse
     */
    public OrderDetailRessponse toOrderDetailResponse(
            TableEntity table,
            InvoiceEntity invoice,
            ReservationEntity reservation,
            List<InvoiceDetailEntity> invoiceDetails) {
        // Khởi tạo response cho order/bàn
        OrderDetailRessponse orderResponse = new OrderDetailRessponse();

        // Gán thông tin bàn (id, tên, trạng thái)
        orderResponse.setTableId(table.getId());
        orderResponse.setTableName(table.getTableName());
        orderResponse.setTableStatus(table.getStatus().name());

        // Nếu có hóa đơn thì gán thông tin hóa đơn (id, trạng thái)
        if (invoice != null) {
            orderResponse.setInvoiceId(invoice.getId());
            orderResponse.setInvoiceStatus(invoice.getStatus().name());
        }

        // Nếu có thông tin đặt bàn thì gán tên, sđt khách, thời gian đặt
        if (reservation != null) {
            orderResponse.setCustomerName(reservation.getCustomerName());
            orderResponse.setCustomerPhone(reservation.getCustomerPhone());
            orderResponse.setReservationDate(reservation.getReservationDate());
        }

        // Nếu có chi tiết hóa đơn thì map sang danh sách món và tính tổng tiền
        if (invoiceDetails != null && !invoiceDetails.isEmpty()) {
            // Duyệt từng chi tiết hóa đơn để tạo danh sách món
            List<OrderDetailRessponse.MenuOrderItemDetail> items = invoiceDetails.stream().map(detail -> {
                // Tạo đối tượng chi tiết món
                OrderDetailRessponse.MenuOrderItemDetail item = new OrderDetailRessponse.MenuOrderItemDetail();
                item.setMenuItemId(detail.getMenuItem().getId()); // id món
                item.setMenuItemName(detail.getMenuItem().getItemName()); // tên món
                item.setQuantity(detail.getQuantity()); // số lượng
                item.setPrice(detail.getPrice()); // đơn giá
                item.setAmount(detail.getPrice() * detail.getQuantity()); // thành tiền
                return item;
            }).collect(Collectors.toList());
            // Gán danh sách món vào response
            orderResponse.setItems(items);
            // Tính tổng tiền tất cả món
            orderResponse.setTotalAmount(
                    items.stream().mapToDouble(OrderDetailRessponse.MenuOrderItemDetail::getAmount).sum());
        }

        // Trả về response đã map đủ thông tin
        return orderResponse;
    }
}