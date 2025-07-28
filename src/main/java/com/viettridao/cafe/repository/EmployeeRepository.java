package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EmployeeRepository
 * Repository thao tác với thực thể EmployeeEntity.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    /**
     * Lấy danh sách nhân viên không bị xóa mềm và tìm kiếm theo từ khóa trong tên.
     *
     * @param keyword  từ khóa tìm kiếm (theo tên nhân viên)
     * @param pageable phân trang
     * @return Page<EmployeeEntity>
     */
    @Query("select e from EmployeeEntity e where e.isDeleted = false and lower(e.fullName) like lower(CONCAT('%', :keyword, '%')) ")
    Page<EmployeeEntity> getAllEmployeesBySearch(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy danh sách tất cả nhân viên không bị xóa mềm.
     *
     * @param pageable phân trang
     * @return Page<EmployeeEntity>
     */
    @Query("select e from EmployeeEntity e where e.isDeleted = false")
    Page<EmployeeEntity> getAllEmployees(Pageable pageable);

    /**
     * Tìm nhân viên theo username tài khoản.
     *
     * @param username tên đăng nhập
     * @return Optional<EmployeeEntity>
     */
    Optional<EmployeeEntity> findByAccount_Username(String username);

    /**
     * Lấy danh sách nhân viên chưa bị xóa mềm.
     *
     * @return List<EmployeeEntity>
     */
    List<EmployeeEntity> findEmployeeByIsDeletedFalse();
}