package com.TATTOO_SHOP.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageUploadService {
    /**
     * Validates MIME type, generates UUID-prefixed filename,
     * saves file to static/images/{subDir}/, and returns the relative URL.
     *
     * @param file   the uploaded file
     * @param subDir "tattoos" or "references"
     * @return relative URL e.g. "/images/tattoos/uuid-name.jpg"
     * @throws IllegalArgumentException if MIME type is unsupported
     * @throws IOException if file write fails
     */
    String saveImage(MultipartFile file, String subDir) throws IOException;
}
