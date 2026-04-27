package com.TATTOO_SHOP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.TATTOO_SHOP.entity.Booking;
import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.service.BookingService;
import com.TATTOO_SHOP.service.ImageUploadService;

@Controller
public class BookingSubmitController {

    @Autowired private BookingService bookingService;
    @Autowired private ImageUploadService imageUploadService;

    @PostMapping(value = "/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String submitBooking(
            @RequestParam("customerName") String customerName,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("tattoo.id") Long tattooId,
            @RequestParam(value = "referenceImage", required = false) MultipartFile referenceImage) {

        Booking booking = new Booking();
        booking.setCustomerName(customerName);
        booking.setPhone(phone);
        booking.setEmail(email);

        Tattoo tattoo = new Tattoo();
        tattoo.setId(tattooId);
        booking.setTattoo(tattoo);

        if (referenceImage != null && !referenceImage.isEmpty()) {
            try {
                String refUrl = imageUploadService.saveImage(referenceImage, "references");
                booking.setReferenceImageUrl(refUrl);
            } catch (Exception e) {
                // ignore reference image error
            }
        }

        bookingService.saveBooking(booking);
        return "redirect:/booking-success";
    }
}
