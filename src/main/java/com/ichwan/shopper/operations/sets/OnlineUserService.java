package com.ichwan.shopper.operations.sets;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OnlineUserService {

    public static final String KEY = "online_users";
    private final StringRedisTemplate redisTemplate;

    public boolean userOnline(String userId) {
        return redisTemplate.opsForSet().add(KEY, userId) == 1;
    }

    public void userOffline(String userId) {
        redisTemplate.opsForSet().remove(KEY, userId);
    }

    public boolean isOnline(String userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(KEY, userId));
    }

    public Set<String> allOnlineUser() {
        return redisTemplate.opsForSet().members(KEY);
    }
}
