package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductRepository
 * Repository cho thực thể ProductEntity.
 * Chịu trách nhiệm truy vấn dữ liệu liên quan đến sản phẩm (Product) từ cơ sở dữ liệu.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    /**
     * Lấy danh sách hàng hóa không bị xóa mềm và tìm kiếm theo từ khóa trong tên.
     *
     * @param keyword  từ khóa tìm kiếm (tên hàng hóa)
     * @param pageable thông tin phân trang
     * @return Page<ProductEntity>
     */
    @Query("select p from ProductEntity p where p.isDeleted = false and p.unit is not null and lower(p.productName) like lower(CONCAT('%', :keyword, '%'))")
    Page<ProductEntity> getAllProductBySearch(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy danh sách tất cả hàng hóa không bị xóa mềm và không null
     *
     * @return List<ProductEntity>
     */
    List<ProductEntity> findAllByIsDeletedFalseAndUnitIsNotNull();

    /**
     * Lấy danh sách tất cả hàng hóa không bị xóa mềm với phân trang.
     *
     * @param pageable thông tin phân trang
     * @return Page<ProductEntity>
     */
    @Query("select p from ProductEntity p where p.isDeleted = false and p.unit is not null")
    Page<ProductEntity> getAllProducts(Pageable pageable);

    /**
     * Kiểm tra tên hàng hóa đã tồn tại (không bị xóa mềm).
     *
     * @param productName tên hàng hóa
     * @return true nếu tên đã tồn tại, ngược lại false
     */
    boolean existsByProductNameAndIsDeletedFalse(String productName);

    /**
     * Kiểm tra trùng tên hàng hóa, loại trừ chính nó (không tính sản phẩm đang cập nhật).
     *
     * @param productName tên hàng hóa
     * @param productId   id sản phẩm đang cập nhật
     * @return true nếu tên đã tồn tại cho sản phẩm khác, ngược lại false
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductEntity p WHERE p.isDeleted = false AND lower(p.productName) = lower(:productName) AND p.id <> :productId")
    boolean existsByProductNameAndIsDeletedFalseAndIdNot(@Param("productName") String productName,
                                                         @Param("productId") Integer productId);
}