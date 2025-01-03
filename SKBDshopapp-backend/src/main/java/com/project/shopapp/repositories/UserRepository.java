package com.project.shopapp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.shopapp.models.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);

  //SELECT * FROM users WHERE phoneNumber=?
  //query command
  @Query("SELECT o FROM User o WHERE o.active = true AND (:keyword IS NULL OR :keyword = '' OR " +
      "o.fullName LIKE %:keyword% " +
      "OR o.address LIKE %:keyword% " +
      "OR o.username LIKE %:keyword% " +
      "OR o.phoneNumber LIKE %:keyword%) " +
      "AND LOWER(o.role.name) = 'user'")
  Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);
}
