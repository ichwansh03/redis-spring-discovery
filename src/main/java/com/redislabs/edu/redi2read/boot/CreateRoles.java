package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.models.Role;
import com.redislabs.edu.redi2read.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CreateRoles implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder().name("admin").build();
            Role userRole = Role.builder().name("user").build();
            roleRepository.save(adminRole);
            roleRepository.save(userRole);
            System.out.println("Created default roles: admin, user");
        }
    }
}
