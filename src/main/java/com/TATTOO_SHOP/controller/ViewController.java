package com.TATTOO_SHOP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TATTOO_SHOP.repository.VideoReviewRepository;
import com.TATTOO_SHOP.repository.TestimonialRepository;
import com.TATTOO_SHOP.service.BookingService;
import com.TATTOO_SHOP.service.TattooService;

@Controller
public class ViewController {

    @Autowired private TattooService tattooService;
    @Autowired private BookingService bookingService;
    @Autowired private VideoReviewRepository videoReviewRepository;
    @Autowired private TestimonialRepository testimonialRepository;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("tattoos", tattooService.getAllTattoos());
        model.addAttribute("videoReviews", videoReviewRepository.findAll());
        model.addAttribute("testimonials", testimonialRepository.findAll());
        return "home";
    }

    @GetMapping("/tattoos")
    public String tattoos(Model model) {
        model.addAttribute("tattoos", tattooService.getAllTattoos());
        return "tattoos";
    }

    @GetMapping("/booking-status")
    public String bookingStatusPage() {
        return "booking-status";
    }

    @GetMapping("/booking-success")
    public String bookingSuccess() {
        return "booking-success";
    }

    @PostMapping("/booking-status")
    public String searchBookingStatus(@RequestParam("query") String query, Model model) {
        model.addAttribute("bookings", bookingService.findByEmailOrPhone(query));
        model.addAttribute("query", query);
        return "booking-status";
    }
}
