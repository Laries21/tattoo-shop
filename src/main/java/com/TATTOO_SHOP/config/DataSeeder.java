package com.TATTOO_SHOP.config;

import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.entity.Testimonial;
import com.TATTOO_SHOP.repository.TattooRepository;
import com.TATTOO_SHOP.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private TattooRepository tattooRepository;

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Override
    public void run(String... args) {
        // Ensure upload directories exist
        ensureDir("uploads/images/tattoos");
        ensureDir("uploads/images/references");
        ensureDir("uploads/images/logo");

        // Copy logo from static if not already in uploads
        copyLogoIfNeeded();

        // Only seed if table is empty
        if (tattooRepository.count() > 0) return;

        tattooRepository.save(tattoo("Dragon Sleeve", "Blackwork", 8500,
            "A powerful full-sleeve dragon design with intricate scales and bold linework. Perfect for those who want a statement piece."));
        tattooRepository.save(tattoo("Rose Mandala", "Geometric", 4500,
            "A stunning fusion of a blooming rose with sacred geometry mandala patterns. Elegant and timeless."));
        tattooRepository.save(tattoo("Watercolor Phoenix", "Watercolor", 6000,
            "A vibrant phoenix rising from the ashes rendered in flowing watercolor strokes. Symbolizes rebirth and strength."));
        tattooRepository.save(tattoo("Minimalist Mountain", "Traditional", 2500,
            "Clean, simple mountain range silhouette with fine linework. Ideal for first-time tattoo enthusiasts."));
        tattooRepository.save(tattoo("Koi Fish", "Neo-Traditional", 5500,
            "A beautifully detailed koi fish with bold colors and neo-traditional shading. Represents perseverance and good fortune."));
        tattooRepository.save(tattoo("Geometric Wolf", "Geometric", 4000,
            "A wolf portrait deconstructed into sharp geometric shapes and triangles. Modern and striking."));
        tattooRepository.save(tattoo("Lotus Flower", "Watercolor", 3500,
            "A delicate lotus flower in soft watercolor washes. Symbolizes purity, enlightenment, and new beginnings."));
        tattooRepository.save(tattoo("Skull & Roses", "Traditional", 5000,
            "Classic American traditional skull surrounded by roses. Bold outlines and vibrant colors that age beautifully."));

        System.out.println("✅ Sample tattoo designs seeded successfully.");

        // Seed default testimonials if empty
        if (testimonialRepository.count() == 0) {
            testimonialRepository.save(testimonial("Arjun Mehta", "Mumbai", 5,
                "Absolutely blown away by the detail and precision. My geometric sleeve is a masterpiece. The team at Clarity Tattoo made the whole experience comfortable and professional."));
            testimonialRepository.save(testimonial("Priya Sharma", "Bangalore", 5,
                "I was nervous about my first tattoo but the artists here made me feel completely at ease. The studio is spotless and the result exceeded every expectation I had."));
            testimonialRepository.save(testimonial("Rahul Verma", "Chennai", 5,
                "The online booking system is so smooth. I uploaded my reference image, got approved quickly, and the final tattoo was even better than the reference. Highly recommend!"));
            System.out.println("✅ Default testimonials seeded.");
        }
    }

    private Tattoo tattoo(String name, String style, double price, String desc) {
        Tattoo t = new Tattoo();
        t.setDesignName(name); t.setStyle(style); t.setPrice(price); t.setDescription(desc);
        return t;
    }

    private Testimonial testimonial(String name, String location, int rating, String text) {
        Testimonial t = new Testimonial();
        t.setCustomerName(name); t.setLocation(location); t.setRating(rating); t.setReviewText(text);
        return t;
    }

    private void ensureDir(String relPath) {
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), relPath);
            Files.createDirectories(dir);
        } catch (Exception e) {
            System.err.println("Could not create dir: " + relPath);
        }
    }

    private void copyLogoIfNeeded() {
        try {
            Path dest = Paths.get(System.getProperty("user.dir"), "uploads", "images", "logo", "clarity-logo.png");
            if (!Files.exists(dest)) {
                // Try to copy from static/images/logo/
                Path src = Paths.get(System.getProperty("user.dir"),
                        "src", "main", "resources", "static", "images", "logo", "clarity-logo.png");
                if (Files.exists(src)) {
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("✅ Logo copied to uploads.");
                } else {
                    System.out.println("⚠️ Logo not found. Place clarity-logo.png in: " + dest.toAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Logo copy failed: " + e.getMessage());
        }
    }
}
