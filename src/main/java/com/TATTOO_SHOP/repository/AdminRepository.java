package com.TATTOO_SHOP.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TATTOO_SHOP.entity.Admin;


public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
}
