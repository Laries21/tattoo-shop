package com.TATTOO_SHOP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.TATTOO_SHOP.entity.Admin;
import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.entity.Testimonial;
import com.TATTOO_SHOP.entity.VideoReview;
import com.TATTOO_SHOP.repository.TestimonialRepository;
import com.TATTOO_SHOP.repository.VideoReviewRepository;
import com.TATTOO_SHOP.service.AdminService;
import com.TATTOO_SHOP.service.BookingService;
import com.TATTOO_SHOP.service.ImageUploadService;
import com.TATTOO_SHOP.service.TattooService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @Autowired private AdminService adminService;
    @Autowired private BookingService bookingService;
    @Autowired private TattooService tattooService;
    @Autowired private ImageUploadService imageUploadService;
    @Autowired private VideoReviewRepository videoReviewRepository;
    @Autowired private TestimonialRepository testimonialRepository;

    // LOGIN PAGE
    @GetMapping("/login")
    public String loginPage() {
        return "admin-login";
    }

    // LOGIN ACTION
    @PostMapping("/login")
    public String login(Admin admin, HttpSession session, Model model) {
        Admin loggedAdmin = adminService.login(admin.getUsername(), admin.getPassword());
        if (loggedAdmin == null) {
            model.addAttribute("error", "Invalid credentials");
            return "admin-login";
        }
        session.setAttribute("admin", loggedAdmin);
        return "redirect:/admin/dashboard";
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("tattoos", tattooService.getAllTattoos());
        model.addAttribute("videoReviews", videoReviewRepository.findAll());
        model.addAttribute("testimonials", testimonialRepository.findAll());
        return "admin-dashboard";
    }

    // APPROVE BOOKING
    @GetMapping("/approve/{id}")
    public String approveBooking(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        bookingService.approveBooking(id);
        return "redirect:/admin/dashboard#bookings";
    }

    // REJECT BOOKING
    @GetMapping("/reject/{id}")
    public String rejectBooking(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        bookingService.rejectBooking(id);
        return "redirect:/admin/dashboard#bookings";
    }

    // ADD TATTOO PAGE
    @GetMapping("/add-tattoo")
    public String addTattooPage(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("tattoo", null);
        return "add-tattoo";
    }

    // SAVE NEW TATTOO
    @PostMapping("/add-tattoo")
    public String saveTattoo(Tattoo tattoo,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        if (image != null && !image.isEmpty()) {
            try {
                String url = imageUploadService.saveImage(image, "tattoos");
                tattoo.setImageUrl(url);
            } catch (Exception e) {
                System.err.println("❌ Image upload failed: " + e.getMessage());
                model.addAttribute("tattoo", tattoo);
                model.addAttribute("imageError", "Image upload failed: " + e.getMessage());
                return "add-tattoo";
            }
        }
        tattooService.addTattoo(tattoo);
        return "redirect:/admin/dashboard";
    }

    // EDIT TATTOO PAGE
    @GetMapping("/edit-tattoo/{id}")
    public String editTattooPage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("tattoo", tattooService.getTattooById(id));
        return "add-tattoo";
    }

    // UPDATE TATTOO
    @PostMapping("/edit-tattoo/{id}")
    public String updateTattoo(@PathVariable Long id,
                               Tattoo tattoo,
                               @RequestParam(value = "image", required = false) MultipartFile image,
                               HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        try {
            tattooService.updateTattoo(id, tattoo, image);
        } catch (Exception e) {
            System.err.println("❌ Update tattoo failed: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // DELETE TATTOO
    @PostMapping("/delete/{id}")
    public String deleteTattoo(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        tattooService.deleteTattoo(id);
        return "redirect:/admin/dashboard#tattoos";
    }

    // DELETE BOOKING
    @PostMapping("/delete-booking/{id}")
    public String deleteBooking(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        bookingService.deleteBooking(id);
        return "redirect:/admin/dashboard#bookings";
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // ADD TESTIMONIAL
    @PostMapping("/add-testimonial")
    public String addTestimonial(@RequestParam("customerName") String name,
                                  @RequestParam("location") String location,
                                  @RequestParam("reviewText") String reviewText,
                                  @RequestParam("rating") int rating,
                                  HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        Testimonial t = new Testimonial();
        t.setCustomerName(name);
        t.setLocation(location);
        t.setReviewText(reviewText);
        t.setRating(rating);
        testimonialRepository.save(t);
        return "redirect:/admin/dashboard#testimonials";
    }

    // EDIT TESTIMONIAL PAGE
    @GetMapping("/edit-testimonial/{id}")
    public String editTestimonialPage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("tattoos", tattooService.getAllTattoos());
        model.addAttribute("videoReviews", videoReviewRepository.findAll());
        model.addAttribute("testimonials", testimonialRepository.findAll());
        testimonialRepository.findById(id).ifPresent(t -> model.addAttribute("editTestimonial", t));
        return "admin-dashboard";
    }

    // UPDATE TESTIMONIAL
    @PostMapping("/edit-testimonial/{id}")
    public String updateTestimonial(@PathVariable Long id,
                                     @RequestParam("customerName") String name,
                                     @RequestParam("location") String location,
                                     @RequestParam("reviewText") String reviewText,
                                     @RequestParam("rating") int rating,
                                     HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        testimonialRepository.findById(id).ifPresent(t -> {
            t.setCustomerName(name);
            t.setLocation(location);
            t.setReviewText(reviewText);
            t.setRating(rating);
            testimonialRepository.save(t);
        });
        return "redirect:/admin/dashboard#testimonials";
    }

    // DELETE TESTIMONIAL
    @PostMapping("/delete-testimonial/{id}")
    public String deleteTestimonial(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        testimonialRepository.deleteById(id);
        return "redirect:/admin/dashboard#testimonials";
    }

    // ADD VIDEO REVIEW
    @PostMapping("/add-video-review")
    public String addVideoReview(@RequestParam("reviewerName") String name,
                                 @RequestParam("rating") int rating,
                                 @RequestParam("feedback") String feedback,
                                 @RequestParam("embedCode") String embedCode,
                                 HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        VideoReview vr = new VideoReview();
        vr.setReviewerName(name);
        vr.setRating(rating);
        vr.setFeedback(feedback);
        vr.setEmbedCode(embedCode);
        videoReviewRepository.save(vr);
        return "redirect:/admin/dashboard";
    }

    // EDIT VIDEO REVIEW PAGE
    @GetMapping("/edit-video-review/{id}")
    public String editVideoReviewPage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("tattoos", tattooService.getAllTattoos());
        model.addAttribute("videoReviews", videoReviewRepository.findAll());
        model.addAttribute("testimonials", testimonialRepository.findAll());
        videoReviewRepository.findById(id).ifPresent(vr -> model.addAttribute("editReview", vr));
        return "admin-dashboard";
    }

    // UPDATE VIDEO REVIEW
    @PostMapping("/edit-video-review/{id}")
    public String updateVideoReview(@PathVariable Long id,
                                    @RequestParam("reviewerName") String name,
                                    @RequestParam("rating") int rating,
                                    @RequestParam("feedback") String feedback,
                                    @RequestParam("embedCode") String embedCode,
                                    HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        videoReviewRepository.findById(id).ifPresent(vr -> {
            vr.setReviewerName(name);
            vr.setRating(rating);
            vr.setFeedback(feedback);
            vr.setEmbedCode(embedCode);
            videoReviewRepository.save(vr);
        });
        return "redirect:/admin/dashboard";
    }

    // DELETE VIDEO REVIEW
    @PostMapping("/delete-video-review/{id}")
    public String deleteVideoReview(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        videoReviewRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}
