package com.coursehub.identity_service.feeding;

import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.security.model.UserRole.ROLE_ADMIN;
import static com.coursehub.commons.security.model.UserStatus.ACTIVE;
import static com.coursehub.identity_service.model.enums.Gender.MALE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminFeeding implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() != 0) {
            return;
        }

        String adminEmail = "mgzlovcontact2@gmail.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin already exists: {}", adminEmail);
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("string"))
                .firstName("Mahabbat")
                .lastName("Gozalov")
                .gender(MALE)
                .phoneNumber("+994-51-532-86-07")
                .aboutMe("System administrator for CourseHub platform.")
                .rating(5.0)
                .isVerified(true)
                .userRole(ROLE_ADMIN)
                .userStatus(ACTIVE)
                .build();

        userRepository.save(admin);
        log.info("Admin created: {}", adminEmail);
    }
}
