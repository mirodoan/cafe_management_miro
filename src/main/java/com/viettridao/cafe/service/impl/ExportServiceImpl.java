package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.ProductEntity;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import com.viettridao.cafe.service.ExportService;

import org.springframework.transaction.annotation.Transactional;

import com.viettridao.cafe.dto.request.export.CreateExportRequest;
import com.viettridao.cafe.dto.request.export.UpdateExportRequest;
import com.viettridao.cafe.mapper.ExportMapper;
import com.viettridao.cafe.model.ExportEntity;
import com.viettridao.cafe.repository.EmployeeRepository;
import com.viettridao.cafe.repository.ExportRepository;
import com.viettridao.cafe.repository.ProductRepository;

/**
 * ExportServiceImpl
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
 * Triển khai Service cho thực thể ExportEntity (Đơn xuất kho).
 * Xử lý nghiệp vụ liên quan tới xuất kho, cập nhật số lượng sản phẩm, kiểm tra tồn kho, và tính tổng tiền xuất.
 */
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExportRepository exportRepository;
    private final ExportMapper exportMapper;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;

    /**
     * Tạo mới một đơn xuất kho, cập nhật số lượng kho, kiểm tra tồn kho và lưu thông tin xuất.
     *
     * @param request thông tin xuất kho cần tạo.
     */
    @Override
    @Transactional
    public void createExport(CreateExportRequest request) {
        // Validate & fetch liên kết trước
        EmployeeEntity employee = findEmployeeOrThrow(request.getEmployeeId());
        ProductEntity product = findProductOrThrow(request.getProductId());

        // Kiểm tra số lượng trong kho trước khi xuất
        Integer currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0;
        if (currentQuantity < request.getQuantity()) {
            throw new IllegalArgumentException(
                    String.format("Không đủ hàng trong kho! Số lượng hiện có: %d, số lượng cần xuất: %d",
                            currentQuantity, request.getQuantity())
            );
        }

        // Cập nhật số lượng sản phẩm trong kho (XUẤT = GIẢM số lượng)
        product.setQuantity(currentQuantity - request.getQuantity());
        productRepository.save(product);

        // Dùng mapper để tạo entity và gán liên kết đã kiểm tra
        ExportEntity exportEntity = exportMapper.toEntity(request);
        exportEntity.setEmployee(employee);
        exportEntity.setProduct(product);

        // Tính toán tổng tiền tự động từ backend
        Double calculatedTotal = request.getUnitExportPrice() * request.getQuantity();
        exportEntity.setTotalAmount(calculatedTotal);

        exportRepository.save(exportEntity);
    }

    /**
     * Lấy thông tin đơn xuất kho để cập nhật theo id.
     *
     * @param id id đơn xuất kho cần lấy.
     * @return UpdateExportRequest thông tin cập nhật đơn xuất kho.
     */
    @Override
    public UpdateExportRequest getUpdateForm(Integer id) {
        ExportEntity exportEntity = findExportOrThrow(id);
        return exportMapper.toUpdateRequest(exportEntity);
    }

    /**
     * Cập nhật thông tin đơn xuất kho.
     *
     * @param id id đơn xuất kho cần cập nhật.
     * @param request thông tin cập nhật đơn xuất kho.
     */
    @Override
    @Transactional
    public void updateExport(Integer id, UpdateExportRequest request) {
        ExportEntity exportEntity = findExportOrThrow(id);

        // Cập nhật dữ liệu chung
        exportMapper.updateEntityFromRequest(request, exportEntity);

        // Validate & cập nhật lại liên kết employee, product nếu cần
        exportEntity.setEmployee(findEmployeeOrThrow(request.getEmployeeId()));
        exportEntity.setProduct(findProductOrThrow(request.getProductId()));

        exportRepository.save(exportEntity);
    }

    /**
     * Xóa mềm đơn xuất kho (đặt isDeleted = true).
     *
     * @param id id đơn xuất kho cần xóa.
     */
    @Override
    @Transactional
    public void deleteExport(Integer id) {
        ExportEntity exportEntity = findExportOrThrow(id);
        exportEntity.setIsDeleted(true);
        exportRepository.save(exportEntity);
    }

    // ======= PRIVATE SUPPORT METHODS =======

    /**
     * Tìm đơn xuất kho theo id hoặc ném lỗi nếu không tồn tại.
     */
    private ExportEntity findExportOrThrow(Integer id) {
        return exportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn xuất"));
    }

    /**
     * Tìm nhân viên theo id hoặc ném lỗi nếu không tồn tại.
     */
    private EmployeeEntity findEmployeeOrThrow(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại!"));
    }

    /**
     * Tìm sản phẩm theo id hoặc ném lỗi nếu không tồn tại.
     */
    private ProductEntity findProductOrThrow(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));
    }
}