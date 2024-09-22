package com.car.foryou.repository;

import com.car.foryou.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailOrUsernameOrPhoneNumber(String email, String username, String phoneNumber);

    @Query(
            value = "SELECT " +
                    "u " +
                    "FROM " +
                    "User u " +
                    "WHERE :username IS NULL " +
                    "OR :username = '' " +
                    "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username,'%'))"
    )
    Page<User> findAllByFilter(@Param("username")String username, Pageable pageable);
}
