package com.coursehub.identity_service.feeding;

import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.Gender;
import com.coursehub.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.coursehub.commons.security.model.UserRole.ROLE_CONTENT_CREATOR;
import static com.coursehub.commons.security.model.UserRole.ROLE_USER;
import static com.coursehub.commons.security.model.UserStatus.ACTIVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAndContentCreatorFeeding implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            this.feed();
        }
    }


    private void feed() {
        String password = passwordEncoder.encode("string");

        List<User> contentCreators = List.of(
                User.builder()
                        .username("creator")
                        .email("mgzlovcontact@gmail.com")
                        .phoneNumber("+994-51-523-81-43")
                        .password(password)
                        .firstName("Tony")
                        .lastName("Stark")
                        .gender(Gender.MALE)
                        .aboutMe("Building suits and teaching engineering concepts.")
                        .rating(4.92)
                        .ratingCount(3000)
                        .userRole(ROLE_CONTENT_CREATOR)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),


                User.builder()
                        .username("ironman")
                        .email("ironman23@gmail.com")
                        .phoneNumber("+994-51-523-81-46")
                        .password(password)
                        .firstName("Tony")
                        .lastName("Stark")
                        .gender(Gender.MALE)
                        .aboutMe("Building suits and teaching engineering concepts.")
                        .rating(4.92)
                        .ratingCount(3000)
                        .userRole(ROLE_CONTENT_CREATOR)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("heisenberg")
                        .email("heisenberg.codes@example.com")
                        .phoneNumber("+994-51-438-92-17")
                        .password(password)
                        .firstName("Walter")
                        .lastName("White")
                        .gender(Gender.MALE)
                        .aboutMe("Breaking bad code habits since 2008.")
                        .rating(4.73)
                        .ratingCount(3000)
                        .userRole(ROLE_CONTENT_CREATOR)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("shelby")
                        .email("t.shelby.dev@example.com")
                        .phoneNumber("+994-51-574-60-29")
                        .password(password)
                        .firstName("Thomas")
                        .lastName("Shelby")
                        .gender(Gender.MALE)
                        .aboutMe("By order of the Peaky Coders.")
                        .rating(4.81)
                        .ratingCount(3000)
                        .userRole(ROLE_CONTENT_CREATOR)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("sheldon")
                        .email("sheldon.science@example.com")
                        .phoneNumber("+994-51-690-45-33")
                        .password(password)
                        .firstName("Sheldon")
                        .lastName("Cooper")
                        .gender(Gender.MALE)
                        .aboutMe("Explaining physics and coding with precision.")
                        .rating(4.67)
                        .ratingCount(3000)
                        .userRole(ROLE_CONTENT_CREATOR)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("jonsnow")
                        .email("jon.snow.tech@example.com")
                        .phoneNumber("+994-51-712-84-09")
                        .password(password)
                        .firstName("Jon")
                        .lastName("Snow")
                        .gender(Gender.MALE)
                        .aboutMe("I know nothing… except backend development.")
                        .rating(4.55)
                        .ratingCount(3000)
                        .userRole(ROLE_CONTENT_CREATOR)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build()
        );

        userRepository.saveAll(contentCreators);


        List<User> users = List.of(

                User.builder()
                        .username("user")
                        .email("mgzlovprsnl@gmail.com")
                        .phoneNumber("+994-51-501-71-41")
                        .password(password)
                        .firstName("David")
                        .lastName("Miller")
                        .gender(Gender.MALE)
                        .aboutMe("Just exploring the platform and learning.")
                        .rating(0.0)
                        .ratingCount(0)
                        .userRole(ROLE_USER)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("david.miller")
                        .email("david.miller@example.com")
                        .phoneNumber("+994-51-501-71-42")
                        .password(password)
                        .firstName("David")
                        .lastName("Miller")
                        .gender(Gender.MALE)
                        .aboutMe("Just exploring the platform and learning.")
                        .rating(0.0)
                        .ratingCount(0)
                        .userRole(ROLE_USER)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("olivia.jackson")
                        .email("olivia.jackson@example.com")
                        .phoneNumber("+994-51-522-64-19")
                        .password(password)
                        .firstName("Olivia")
                        .lastName("Jackson")
                        .gender(Gender.FEMALE)
                        .aboutMe("Love discovering new content every day.")
                        .rating(0.0)
                        .ratingCount(0)
                        .userRole(ROLE_USER)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("daniel.harris")
                        .email("daniel.harris@example.com")
                        .phoneNumber("+994-51-580-12-88")
                        .password(password)
                        .firstName("Daniel")
                        .lastName("Harris")
                        .gender(Gender.MALE)
                        .aboutMe("Learning backend and enjoying the journey.")
                        .rating(0.0)
                        .ratingCount(0)
                        .userRole(ROLE_USER)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("grace.lewis")
                        .email("grace.lewis@example.com")
                        .phoneNumber("+994-51-619-42-76")
                        .password(password)
                        .firstName("Grace")
                        .lastName("Lewis")
                        .gender(Gender.FEMALE)
                        .aboutMe("Passionate about creativity and tech.")
                        .rating(0.0)
                        .ratingCount(0)
                        .userRole(ROLE_USER)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build(),

                User.builder()
                        .username("samuel.lee")
                        .email("samuel.lee@example.com")
                        .phoneNumber("+994-51-693-15-04")
                        .password(password)
                        .firstName("Samuel")
                        .lastName("Lee")
                        .gender(Gender.MALE)
                        .aboutMe("Enjoying the platform casually.")
                        .rating(0.0)
                        .ratingCount(0)
                        .userRole(ROLE_USER)
                        .userStatus(ACTIVE)
                        .isVerified(true)
                        .build()
        );

        userRepository.saveAll(users);
    }
}