package com.coursehub.identity_service;

import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.UserRole;
import com.coursehub.identity_service.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.coursehub.identity_service.model.enums.Gender.MALE;
import static com.coursehub.identity_service.model.enums.UserRole.ROLE_ADMIN;
import static com.coursehub.identity_service.model.enums.UserRole.ROLE_INSTRUCTOR;
import static com.coursehub.identity_service.model.enums.UserStatus.ACTIVE;
import static java.lang.Boolean.TRUE;

@SpringBootApplication
@EnableDiscoveryClient
public class IdentityServiceApplication implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(IdentityServiceApplication.class);


    public IdentityServiceApplication(PasswordEncoder passwordEncoder, IUserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }


    private void createUserIfNotExists(String username,
                                       String email,
                                       String phoneNumber,
                                       String firstName,
                                       String lastName,
                                       UserRole role
    ) {

        if (!userRepository.existsByUsername(username)) {

            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("SecurePass!234"))
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .firstName(firstName)
                    .lastName(lastName)
                    .userStatus(ACTIVE)
                    .isVerified(TRUE)
                    .userRole(role)
                    .gender(MALE)
                    .activationCode(UUID.randomUUID().toString())
                    .build();

            userRepository.save(user);
            log.info("✅ User with username: {}, role: {}", username, role);

        } else {
            log.info("ℹ️ User with username: {}, role: {} already exists, skipping creation", username, role);
        }
    }

    @Override
    public void run(String... args) {

        createUserIfNotExists(
                "Admin",
                "mgzlovcode@gmail.com",
                "0515328608",
                "Mahabbat",
                "Gezalov",
                ROLE_ADMIN
        );


        createUserIfNotExists(
                "BlessedController",
                "mgzlovcontact@gmail.com",
                "0515328607",
                "Məhəbbət",
                "Gözəlov",
                ROLE_INSTRUCTOR
        );
    }
}
