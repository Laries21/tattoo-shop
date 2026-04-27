package com.TATTOO_SHOP.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TATTOO_SHOP.entity.Tattoo;

public interface TattooRepository extends JpaRepository<Tattoo, Long> {
}
