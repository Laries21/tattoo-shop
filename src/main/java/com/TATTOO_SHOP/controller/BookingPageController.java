package com.TATTOO_SHOP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.TATTOO_SHOP.entity.Booking;
import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.service.TattooService;

@Controller
public class BookingPageController {

    @Autowired
    private TattooService tattooService;

    // 📌 BOOKING PAGE
    @GetMapping("/book/{id}")
    public String bookingForm(@PathVariable Long id, Model model) {

        // 1️⃣ Get tattoo from DB
        Tattoo tattoo = tattooService.getTattooById(id);

        // 2️⃣ Create booking object
        Booking booking = new Booking();
        booking.setTattoo(tattoo);

        // 3️⃣ Send data to Thymeleaf
        model.addAttribute("booking", booking);
        model.addAttribute("tattoo", tattoo);

        return "booking-form";   // templates/booking-form.html
    }
}
