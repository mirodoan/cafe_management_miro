package com.viettridao.cafe.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.viettridao.cafe.dto.response.warehouse_transaction.WarehouseTransactionPageResponse;
import com.viettridao.cafe.service.WarehouseTransactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.viettridao.cafe.dto.response.warehouse_transaction.WarehouseTransactionResponse;
import com.viettridao.cafe.mapper.WarehouseTransactionMapper;
import com.viettridao.cafe.repository.ExportRepository;
import com.viettridao.cafe.repository.ImportRepository;

/**
 * WarehouseTransactionServiceImpl
 *
 * Version 1.0
 *
 * Date: 18-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 18-07-2025   mirodoan    Create
 *
 * Service cho quản lý giao dịch nhập xuất trong kho.
 * Chịu trách nhiệm xử lý logic nghiệp vụ liên quan đến giao dịch nhập xuất trong kho hàng.
 */
@Service
@RequiredArgsConstructor
public class WarehouseTransactionServiceImpl implements WarehouseTransactionService {

    // Inject các thành phần cần thiết
    private final WarehouseTransactionMapper warehouseTransactionMapper;
    private final ImportRepository importRepository;
    private final ExportRepository exportRepository;

    /**
     * Lấy danh sách giao dịch nhập/xuất, có hỗ trợ tìm kiếm theo tên hàng hóa và phân trang thủ công.
     *
     * @param keyword từ khóa tìm kiếm theo tên hàng hóa
     * @param page    số trang hiện tại (bắt đầu từ 0)
     * @param size    số bản ghi mỗi trang
     * @return đối tượng chứa danh sách phân trang và metadata
     */
    @Override
    public WarehouseTransactionPageResponse getTransactions(String keyword, int page, int size) {
        // Khởi tạo danh sách kết quả tổng hợp từ cả đơn nhập và đơn xuất
        List<WarehouseTransactionResponse> allTransactions = new ArrayList<>();

        // 🔹 Lấy danh sách đơn nhập chưa bị xóa mềm, map thành DTO và lọc theo keyword
        importRepository.findAllByIsDeletedFalse().forEach(imp -> {
            try {
                WarehouseTransactionResponse tx = warehouseTransactionMapper.fromImport(imp);

                // Nếu tên hàng hóa khớp với từ khóa, thêm vào danh sách
                if (matchesKeyword(tx, keyword)) {
                    allTransactions.add(tx);
                }
            } catch (Exception e) {
                System.err.println("Lỗi mapping import: " + e.getMessage());
            }
        });

        // 🔹 Lấy danh sách đơn xuất chưa bị xóa mềm, map thành DTO và lọc theo keyword
        exportRepository.findAllByIsDeletedFalse().forEach(exp -> {
            try {
                WarehouseTransactionResponse tx = warehouseTransactionMapper.fromExport(exp);

                // Nếu tên hàng hóa khớp với từ khóa, thêm vào danh sách
                if (matchesKeyword(tx, keyword)) {
                    allTransactions.add(tx);
                }
            } catch (Exception e) {
                System.err.println("Lỗi mapping export: " + e.getMessage());
            }
        });

        // 🔹 Sắp xếp danh sách theo ngày mới nhất (importDate hoặc exportDate giảm dần)
        allTransactions.sort(Comparator.comparing(
                tx -> tx.getImportDate() != null ? tx.getImportDate() : tx.getExportDate(),
                Comparator.reverseOrder())
        );

        // 🔹 Bắt đầu xử lý phân trang thủ công
        int totalElements = allTransactions.size();              // Tổng số giao dịch sau lọc
        int fromIndex = page * size;                             // Vị trí bắt đầu của trang hiện tại
        int toIndex = Math.min(fromIndex + size, totalElements); // Vị trí kết thúc (không vượt quá tổng)

        // Nếu fromIndex vượt quá totalElements, trả danh sách rỗng
        List<WarehouseTransactionResponse> pagedList = new ArrayList<>();
        if (fromIndex < totalElements) {
            pagedList = allTransactions.subList(fromIndex, toIndex);
        }

        // 🔹 Tạo đối tượng response và gán các thông tin phân trang
        WarehouseTransactionPageResponse response = new WarehouseTransactionPageResponse();
        response.setPageNumber(page);                                 // Trang hiện tại
        response.setPageSize(size);                                   // Kích thước trang
        response.setTotalElements(totalElements);                     // Tổng số bản ghi sau lọc
        response.setTotalPages((int) Math.ceil((double) totalElements / size)); // Tổng số trang
        response.setTransactions(pagedList);                          // Danh sách kết quả thực tế của trang

        return response;
    }

    /**
     * Hàm hỗ trợ: kiểm tra xem sản phẩm trong giao dịch có chứa từ khóa tìm kiếm hay không
     *
     * @param tx      DTO giao dịch kho
     * @param keyword từ khóa tìm kiếm (không phân biệt hoa thường)
     * @return true nếu khớp, false nếu không
     */
    private boolean matchesKeyword(WarehouseTransactionResponse tx, String keyword) {
        if (keyword == null || keyword.isBlank()) return true; // Nếu không có từ khóa → không lọc
        return tx.getProductName() != null &&
                tx.getProductName().toLowerCase().contains(keyword.toLowerCase());
    }
}