package com.TATTOO_SHOP.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.repository.BookingRepository;
import com.TATTOO_SHOP.repository.TattooRepository;

@Service
public class TattooServiceImpl implements TattooService {

    @Autowired
    private TattooRepository tattooRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public Tattoo addTattoo(Tattoo tattoo) {
        return tattooRepository.save(tattoo);
    }

    @Override
    public List<Tattoo> getAllTattoos() {
        return tattooRepository.findAll();
    }

    @Override
    public void deleteTattoo(Long id) {
        // Delete associated bookings first to avoid FK constraint
        bookingRepository.findAll().stream()
            .filter(b -> b.getTattoo() != null && id.equals(b.getTattoo().getId()))
            .forEach(b -> bookingRepository.deleteById(b.getId()));
        tattooRepository.deleteById(id);
    }

    @Override
    public Tattoo getTattooById(Long id) {
        return tattooRepository.findById(id).orElse(null);
    }

    @Override
    public Tattoo updateTattoo(Long id, Tattoo updated, MultipartFile image) throws IOException {
        Tattoo existing = tattooRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tattoo not found: " + id));

        existing.setDesignName(updated.getDesignName());
        existing.setStyle(updated.getStyle());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.saveImage(image, "tattoos");
            existing.setImageUrl(imageUrl);
        }

        return tattooRepository.save(existing);
    }
}
