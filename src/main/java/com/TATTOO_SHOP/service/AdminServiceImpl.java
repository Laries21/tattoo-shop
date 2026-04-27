package com.TATTOO_SHOP.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TATTOO_SHOP.entity.Admin;
import com.TATTOO_SHOP.repository.AdminRepository;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Admin login(String username, String rawPassword) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) return null;

        String stored = admin.getPassword();

        // Case 1: stored password is a BCrypt hash
        if (stored != null && stored.startsWith("$2a$") || (stored != null && stored.startsWith("$2b$"))) {
            return passwordEncoder.matches(rawPassword, stored) ? admin : null;
        }

        // Case 2: stored password is plain text (not yet hashed)
        // Auto-upgrade: compare plain text, then save BCrypt hash
        if (stored != null && stored.equals(rawPassword)) {
            admin.setPassword(passwordEncoder.encode(rawPassword));
            adminRepository.save(admin);  // upgrade to BCrypt in DB
            return admin;
        }

        return null;
    }
}
