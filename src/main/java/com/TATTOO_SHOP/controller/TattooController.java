package com.TATTOO_SHOP.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.service.TattooService;

@RestController
@RequestMapping("/api/tattoos")  //http://localhost:8080/api/tattoos
public class TattooController {

    @Autowired
    private TattooService tattooService;

    // Get all tattoo designs (public)
    @GetMapping
    public List<Tattoo> getAllTattoos() {
        return tattooService.getAllTattoos();
    }
}
