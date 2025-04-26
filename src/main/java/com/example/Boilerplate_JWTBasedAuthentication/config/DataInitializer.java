package com.example.Boilerplate_JWTBasedAuthentication.config;

import com.example.Boilerplate_JWTBasedAuthentication.entity.Role;
import com.example.Boilerplate_JWTBasedAuthentication.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            createRoleIfNotFound(roleRepository, "ROLE_USER");
            createRoleIfNotFound(roleRepository, "ROLE_ADMIN");
        };
    }

    private void createRoleIfNotFound(RoleRepository roleRepository, String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder().name(roleName).build();
            roleRepository.save(role);
        }
    }
}
