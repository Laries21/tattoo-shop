package com.TATTOO_SHOP.service;

import java.util.List;

import com.TATTOO_SHOP.entity.Booking;

public interface BookingService {
    Booking createBooking(Booking booking);
    Booking saveBooking(Booking booking);
    List<Booking> getAllBookings();
    Booking approveBooking(Long id);
    Booking rejectBooking(Long id);
    List<Booking> findByEmailOrPhone(String query);
    void deleteBooking(Long id);
}
