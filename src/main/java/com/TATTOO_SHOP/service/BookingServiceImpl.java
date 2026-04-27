package com.TATTOO_SHOP.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TATTOO_SHOP.entity.Booking;
import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.repository.BookingRepository;
import com.TATTOO_SHOP.repository.TattooRepository;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TattooRepository tattooRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public Booking createBooking(Booking booking) {
        Long tattooId = booking.getTattoo().getId();
        Tattoo tattoo = tattooRepository.findById(tattooId)
                .orElseThrow(() -> new RuntimeException("Tattoo not found"));

        booking.setTattoo(tattoo);
        booking.setStatus("PENDING");
        booking.setBookingDate(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        Booking saved = bookingRepository.save(booking);

        emailService.sendBookingPendingEmail(
                saved.getEmail(),
                saved.getCustomerName(),
                tattoo.getDesignName(),
                saved.getBookingDate()
        );

        return saved;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking approveBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("APPROVED");
        Booking updated = bookingRepository.save(booking);

        emailService.sendBookingApprovedEmail(
                updated.getEmail(),
                updated.getCustomerName(),
                updated.getTattoo().getDesignName(),
                updated.getTattoo().getPrice(),
                updated.getBookingDate()
        );

        return updated;
    }

    @Override
    public Booking rejectBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("REJECTED");
        Booking updated = bookingRepository.save(booking);

        emailService.sendBookingRejectedEmail(
                updated.getEmail(),
                updated.getCustomerName(),
                updated.getTattoo().getDesignName()
        );

        return updated;
    }

    @Override
    public List<Booking> findByEmailOrPhone(String query) {
        return bookingRepository.findByEmailOrPhone(query);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Booking saveBooking(Booking booking) {
        return createBooking(booking);
    }
}
