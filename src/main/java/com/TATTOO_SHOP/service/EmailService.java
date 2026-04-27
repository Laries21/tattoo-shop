package com.TATTOO_SHOP.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendBookingApprovedEmail(
            String to,
            String customerName,
            String tattooName,
            double price,
            String bookingDate
    );

    void sendBookingPendingEmail(
            String to,
            String customerName,
            String tattooName,
            String bookingDate
    );

    void sendBookingRejectedEmail(
            String to,
            String customerName,
            String tattooName
    );
}
