package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AccountRepository
 * Repository thao tác với thực thể AccountEntity.
 * Chịu trách nhiệm truy vấn dữ liệu tài khoản từ database.
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    /**
     * Tìm tài khoản theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return Optional<AccountEntity>
     */
    @Query("SELECT a FROM AccountEntity a WHERE a.username = :username AND a.isDeleted = false")
    Optional<AccountEntity> findByUsername(String username);
}