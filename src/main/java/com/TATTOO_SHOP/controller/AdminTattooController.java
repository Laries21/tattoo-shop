package com.TATTOO_SHOP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.TATTOO_SHOP.entity.Tattoo;
import com.TATTOO_SHOP.service.TattooService;

@RestController
@RequestMapping("/api/admin")
public class AdminTattooController {

    @Autowired
    private TattooService tattooService;

    // ➕ ADD TATTOO (API)
    @PostMapping("/tattoo")
    public Tattoo addTattoo(@RequestBody Tattoo tattoo) {
        return tattooService.addTattoo(tattoo);
    }

    // ❌ DELETE TATTOO
    @DeleteMapping("/tattoo/{id}")
    public void deleteTattoo(@PathVariable Long id) {
        tattooService.deleteTattoo(id);
    }
}
