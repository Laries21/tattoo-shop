package com.TATTOO_SHOP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TATTOO_SHOP.entity.Admin;
import com.TATTOO_SHOP.security.JwtUtil;
import com.TATTOO_SHOP.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ LOGIN → JWT
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Admin admin) {

        Admin loggedAdmin =
                adminService.login(admin.getUsername(), admin.getPassword());

        if (loggedAdmin == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(loggedAdmin.getUsername());
        return ResponseEntity.ok(token);
    }
}
