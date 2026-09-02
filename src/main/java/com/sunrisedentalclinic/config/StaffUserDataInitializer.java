package com.sunrisedentalclinic.config;

import com.sunrisedentalclinic.model.StaffUser;
import com.sunrisedentalclinic.repository.StaffUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class StaffUserDataInitializer {

    @Bean
    CommandLineRunner seedDefaultStaffUser(
            StaffUserRepository staffUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.default-staff.username:admin}") String username,
            @Value("${app.default-staff.password:ChangeMe123!}") String password) {
        return args -> {
            if (staffUserRepository.findByUsername(username).isEmpty()) {
                staffUserRepository.save(new StaffUser(username, passwordEncoder.encode(password)));
            }
        };
    }
}
