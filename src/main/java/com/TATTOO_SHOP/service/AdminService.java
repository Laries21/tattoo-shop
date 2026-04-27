package com.TATTOO_SHOP.service;

import com.TATTOO_SHOP.entity.Admin;

public interface AdminService {

    Admin login(String username, String password);
}
