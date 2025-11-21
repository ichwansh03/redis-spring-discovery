package com.redislabs.edu.redi2read;

import jdk.jshell.EvalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap;
import java.util.Map;

@RestController
@RequestMapping("/api/redis")
public class HelloRedisController {

    @Autowired
    private RedisTemplate<String, String> template;

    public static final String STRING_KEY_PREFIX = "redi2read:strings:";

    @PostMapping("/strings")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, String> setString(@RequestBody Map.Entry<String, String> entry) {
        template.opsForValue().set(STRING_KEY_PREFIX+entry.getKey(), entry.getValue());
        return entry;
    }

    @GetMapping("/strings/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, String> getString(@PathVariable("key") String key) {
        String value = template.opsForValue().get(STRING_KEY_PREFIX + key);

        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "key not found");
        }

        return new AbstractMap.SimpleEntry<String, String>(key, value);
    }
}
