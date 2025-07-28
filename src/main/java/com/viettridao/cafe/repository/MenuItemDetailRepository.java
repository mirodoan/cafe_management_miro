package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.MenuDetailEntity;
import com.viettridao.cafe.model.MenuKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * MenuItemDetailRepository
 * Repository thao tác với chi tiết thực đơn (MenuDetailEntity).
 */
@Repository
public interface MenuItemDetailRepository extends JpaRepository<MenuDetailEntity, MenuKey> {

    /**
     * Xóa chi tiết thực đơn theo id của món ăn.
     *
     * @param menuItemId id của món ăn
     */
    void deleteByMenuItem_Id(Integer menuItemId);
}