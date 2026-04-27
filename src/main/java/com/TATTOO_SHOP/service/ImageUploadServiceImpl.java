package com.TATTOO_SHOP.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    // Use absolute path based on user.dir (project root)
    private Path getUploadRoot() {
        return Paths.get(System.getProperty("user.dir"), "uploads", "images");
    }

    @Override
    public String saveImage(MultipartFile file, String subDir) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "upload.jpg";
        }
        // Sanitize filename
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        String uniqueFilename = UUID.randomUUID().toString().substring(0, 8) + "-" + originalFilename;

        // Absolute path
        Path uploadDir = getUploadRoot().resolve(subDir);
        Files.createDirectories(uploadDir);

        Path targetPath = uploadDir.resolve(uniqueFilename);

        // Use Files.copy with InputStream — works reliably with absolute paths
        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("✅ Image saved: " + targetPath.toAbsolutePath());
        return "/uploads/images/" + subDir + "/" + uniqueFilename;
    }
}
