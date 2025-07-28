package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.product.CreateProductRequest;
import com.viettridao.cafe.dto.request.product.UpdateProductRequest;
import com.viettridao.cafe.dto.response.product.ProductPageResponse;
import com.viettridao.cafe.dto.response.product.ProductResponse;
import com.viettridao.cafe.model.ProductEntity;
import java.util.List;

/**
 * ProductService
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
 */
public interface ProductService {

    /**
     * Lấy danh sách tất cả sản phẩm (không phân trang).
     * @return Danh sách các sản phẩm.
     */
    List<ProductResponse> getAllProducts();

    /**
     * Lấy danh sách sản phẩm có phân trang và tìm kiếm theo từ khóa.
     * @param keyword Từ khóa tìm kiếm theo tên sản phẩm.
     * @param page    Số trang cần lấy.
     * @param size    Số lượng bản ghi trên mỗi trang.
     * @return Đối tượng ProductPageResponse chứa danh sách sản phẩm và thông tin phân trang.
     */
    ProductPageResponse getAllProducts(String keyword, int page, int size);

    /**
     * Kiểm tra tên sản phẩm đã tồn tại và chưa bị xóa.
     * @param productName tên sản phẩm
     * @return true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByProductNameAndIsDeletedFalse(String productName);

    /**
     * Kiểm tra trùng tên sản phẩm, loại trừ chính nó (không tính sản phẩm đang cập nhật).
     * @param productName tên sản phẩm
     * @param productId   id sản phẩm cần loại trừ
     * @return true nếu đã tồn tại tên sản phẩm khác với id này
     */
    boolean existsByProductNameAndIsDeletedFalseExceptId(String productName, Integer productId);

    /**
     * Tạo mới một sản phẩm.
     * @param request Đối tượng chứa thông tin cần thiết để tạo sản phẩm mới.
     * @return Thực thể ProductEntity vừa được tạo.
     */
    ProductEntity createProduct(CreateProductRequest request);

    /**
     * Xóa một sản phẩm dựa trên ID.
     * @param id ID của sản phẩm cần xóa.
     */
    void deleteProduct(Integer id);

    /**
     * Cập nhật thông tin sản phẩm.
     * @param request Đối tượng chứa thông tin cần cập nhật cho sản phẩm.
     */
    void updateProduct(UpdateProductRequest request);

    /**
     * Lấy thông tin chi tiết của một sản phẩm dựa trên ID.
     * @param id ID của sản phẩm cần lấy thông tin.
     * @return Thực thể ProductEntity tương ứng với ID.
     */
    ProductEntity getProductById(Integer id);
}