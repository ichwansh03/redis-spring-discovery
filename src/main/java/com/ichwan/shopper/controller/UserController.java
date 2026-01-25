package com.ichwan.shopper.controller;

import com.ichwan.shopper.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/session")
public class UserController {

    /**
     * Login - Create Session
     */
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String username,
            @RequestParam String email,
            HttpSession session) {

        // Buat user session
        User User = new User(
                "USER_" + System.currentTimeMillis(),
                username,
                email,
                "USER"
        );

        // Simpan ke session
        session.setAttribute("user", User);
        session.setAttribute("loginTime", System.currentTimeMillis());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Login berhasil");
        response.put("sessionId", session.getId());
        response.put("user", User);

        return response;
    }

    /**
     * Get Current Session
     */
    @GetMapping("/current")
    public Map<String, Object> getCurrentSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long loginTime = (Long) session.getAttribute("loginTime");

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("user", user);
        response.put("loginTime", loginTime);
        response.put("maxInactiveInterval", session.getMaxInactiveInterval());

        return response;
    }

    /**
     * Update Session Data
     */
    @PutMapping("/update")
    public Map<String, Object> updateSession(
            @RequestParam String email,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Session tidak ditemukan. Silakan login.");
            return error;
        }

        user.setEmail(email);
        session.setAttribute("user", user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Session berhasil diupdate");
        response.put("user", user);

        return response;
    }

    /**
     * Logout - Destroy Session
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        String sessionId = session.getId();
        session.invalidate();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logout berhasil");
        response.put("sessionId", sessionId);

        return response;
    }

    /**
     * Add Custom Attribute
     */
    @PostMapping("/attribute")
    public Map<String, Object> addAttribute(
            @RequestParam String key,
            @RequestParam String value,
            HttpSession session) {

        session.setAttribute(key, value);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Attribute ditambahkan");
        response.put("key", key);
        response.put("value", value);

        return response;
    }

    /**
     * Get Session Attributes
     */
    @GetMapping("/attributes")
    public Map<String, Object> getAttributes(HttpSession session) {
        Map<String, Object> attributes = new HashMap<>();

        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> attributes.put(name, session.getAttribute(name)));

        return attributes;
    }

}
