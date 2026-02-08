package com.ichwan.shopper.operations.sets;

import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/online")
public class OnlineUserController {

    private final OnlineUserService service;

    public OnlineUserController(OnlineUserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public String login(@RequestParam String userId) {
        return service.userOnline(userId) ? "User logged in" : "Already online";
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String userId) {
        service.userOffline(userId);
        return "User logged out";
    }

    @GetMapping
    public Set<String> onlineUsers() {
        return service.allOnlineUser();
    }
}

