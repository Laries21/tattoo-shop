package com.TATTOO_SHOP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.TATTOO_SHOP.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.email = :query OR b.phone = :query")
    List<Booking> findByEmailOrPhone(@Param("query") String query);
}
