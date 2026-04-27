package com.TATTOO_SHOP.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.TATTOO_SHOP.entity.Tattoo;

public interface TattooService {
    Tattoo addTattoo(Tattoo tattoo);
    List<Tattoo> getAllTattoos();
    void deleteTattoo(Long id);
    Tattoo getTattooById(Long id);
    Tattoo updateTattoo(Long id, Tattoo tattoo, MultipartFile image) throws IOException;
}
