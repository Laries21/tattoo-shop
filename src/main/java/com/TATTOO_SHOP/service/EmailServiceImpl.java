package com.TATTOO_SHOP.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendBookingApprovedEmail(String to, String customerName,
                                          String tattooName, double price, String bookingDate) {
        String body = """
                ════════════════════════════════════════
                        CLARITY TATTOO STUDIO
                ════════════════════════════════════════

                Dear %s,

                Great news! Your tattoo booking has been
                APPROVED by our team.

                ─────────────────────────────────────────
                  BOOKING DETAILS
                ─────────────────────────────────────────
                  Design   : %s
                  Price    : ₹%.0f
                  Date     : %s
                ─────────────────────────────────────────

                Please arrive 10 minutes before your
                scheduled time. Bring a valid ID.

                If you need to reschedule or have any
                questions, contact us at:
                  📞  +91 87549 21668
                  ✉️  karthi105282@gmail.com
                  📸  @clarity_tattoo

                We look forward to seeing you!

                Warm regards,
                Clarity Tattoo Team
                ════════════════════════════════════════
                """.formatted(customerName, tattooName, price, bookingDate);

        sendEmail(to, "✅ Booking Approved — Clarity Tattoo", body);
    }

    @Override
    public void sendBookingPendingEmail(String to, String customerName,
                                         String tattooName, String bookingDate) {
        String body = """
                ════════════════════════════════════════
                        CLARITY TATTOO STUDIO
                ════════════════════════════════════════

                Dear %s,

                Thank you for choosing Clarity Tattoo!
                We have received your booking request
                and it is currently under review.

                ─────────────────────────────────────────
                  BOOKING DETAILS
                ─────────────────────────────────────────
                  Design   : %s
                  Submitted: %s
                  Status   : PENDING REVIEW
                ─────────────────────────────────────────

                Our team will review your request and
                send you a confirmation email shortly.

                You can track your booking status at:
                  🌐  localhost:8080/booking-status

                Questions? Reach us at:
                  📞  +91 87549 21668
                  ✉️  karthi105282@gmail.com

                Thank you for your patience!

                Warm regards,
                Clarity Tattoo Team
                ════════════════════════════════════════
                """.formatted(customerName, tattooName, bookingDate);

        sendEmail(to, "🕒 Booking Received — Clarity Tattoo", body);
    }

    @Override
    public void sendBookingRejectedEmail(String to, String customerName, String tattooName) {
        String body = """
                ════════════════════════════════════════
                        CLARITY TATTOO STUDIO
                ════════════════════════════════════════

                Dear %s,

                We regret to inform you that your
                booking request could not be approved
                at this time.

                ─────────────────────────────────────────
                  BOOKING DETAILS
                ─────────────────────────────────────────
                  Design   : %s
                  Status   : NOT APPROVED
                ─────────────────────────────────────────

                This may be due to scheduling conflicts
                or availability. We encourage you to
                submit a new booking request or contact
                us directly to discuss alternatives.

                  📞  +91 87549 21668
                  ✉️  karthi105282@gmail.com
                  📸  @clarity_tattoo

                We hope to serve you soon!

                Warm regards,
                Clarity Tattoo Team
                ════════════════════════════════════════
                """.formatted(customerName, tattooName);

        sendEmail(to, "Booking Update — Clarity Tattoo", body);
    }
}
