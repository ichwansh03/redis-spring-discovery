package com.redislabs.edu.redi2read.boot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Role;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.RoleRepository;
import com.redislabs.edu.redi2read.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@Order(1)
public class CreateRoles implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>> Checking for existing roles...");
        log.info("check user count: {}", userRepository.count());
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findFirstByName("admin");
            Role userRole = roleRepository.findFirstByName("customer");

            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<List<User>> typeReference = new TypeReference<>() {
                };
                InputStream inputStream = getClass().getResourceAsStream("/data/users/users.json");
                List<User> users = mapper.readValue(inputStream, typeReference);
                log.info("check user count after read: {}", users.size());
                users.forEach((user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.addRole(userRole);
                    userRepository.save(user);
                }));
                log.info(">>> {} users read from JSON file", users.size());
            } catch (IOException e) {
                log.info(">>> unable to read users: {}", e.getMessage());
            }

            User adminUser = new User();
            adminUser.setName("Admin User");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("foo bar"));
            adminUser.addRole(adminRole);

            userRepository.save(adminUser);
            log.info(">>> Admin user created: {}", adminUser.getEmail());
        }
    }
}
