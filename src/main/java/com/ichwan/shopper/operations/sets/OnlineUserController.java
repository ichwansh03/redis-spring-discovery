package com.ichwan.shopper.operations.sets;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/online")
@RequiredArgsConstructor
public class OnlineUserController {

    private final OnlineUserService service;

    @PostMapping("/login")
    public String login(@RequestParam String userId) {
        return service.userOnline(userId) ? "User logged in" : "Already online";
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String userId) {
        service.userOffline(userId);
        return "User logged out";
    }

    @GetMapping("/{id}")
    public String isOnline(@RequestParam String userId) {
        boolean online = service.isOnline(userId);
        return online ? "User is online" : "user is offline";
    }

    @GetMapping
    public Set<String> onlineUsers() {
        return service.allOnlineUser();
    }
}

