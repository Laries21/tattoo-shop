package com.TATTOO_SHOP.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.TATTOO_SHOP.entity.Booking;
import com.TATTOO_SHOP.service.BookingService;

@RestController
@RequestMapping("/api/admin")
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;

    // ADMIN – VIEW ALL BOOKINGS
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // ADMIN – APPROVE BOOKING
    @PutMapping("/booking/{id}/approve")
    public Booking approveBooking(@PathVariable Long id) {
        return bookingService.approveBooking(id);
    }
}
