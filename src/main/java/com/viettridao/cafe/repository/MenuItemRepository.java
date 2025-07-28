package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.MenuItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MenuItemRepository
 * Repository thao tác với món ăn (MenuItemEntity).
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Integer> {

    /**
     * Tìm tất cả menu items không bị xóa mềm.
     *
     * @return List<MenuItemEntity>
     */
    List<MenuItemEntity> findByIsDeletedFalseOrIsDeletedIsNull();

    /**
     * Tìm tất cả menu items không bị xóa mềm với phân trang, có tìm kiếm theo tên món ăn.
     *
     * @param keyword  từ khóa tìm kiếm (tên món ăn)
     * @param pageable phân trang
     * @return Page<MenuItemEntity>
     */
    @Query("select m from MenuItemEntity m where m.isDeleted = false and lower(m.itemName) like lower(CONCAT('%', :keyword, '%')) ")
    Page<MenuItemEntity> getAllByMenuItems(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Tìm tất cả menu items không bị xóa mềm với phân trang.
     *
     * @param pageable phân trang
     * @return Page<MenuItemEntity>
     */
    @Query("select m from MenuItemEntity m where m.isDeleted = false")
    Page<MenuItemEntity> getAllByMenuItems(Pageable pageable);

    /**
     * Kiểm tra tên thực đơn đã tồn tại (không bị xóa mềm).
     *
     * @param itemName tên món ăn
     * @return true nếu tên món đã tồn tại, ngược lại false
     */
    boolean existsByItemNameAndIsDeletedFalse(String itemName);

    /**
     * Kiểm tra trùng tên thực đơn, loại trừ chính nó (không tính món đang cập nhật).
     *
     * @param itemName   tên món ăn
     * @param menuItemId id của món ăn đang cập nhật
     * @return true nếu tên món đã tồn tại cho món khác, ngược lại false
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MenuItemEntity m WHERE m.isDeleted = false AND lower(m.itemName) = lower(:itemName) AND m.id <> :menuItemId")
    boolean existsByItemNameAndIsDeletedFalseAndIdNot(@Param("itemName") String itemName,
                                                      @Param("menuItemId") Integer menuItemId);

}