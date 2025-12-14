package com.coursehub.identity_service.security;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();

        boolean emailVerified = Boolean.TRUE.equals(oidcUser.getEmailVerified());

        if (email == null || !emailVerified) {
            throw new OAuth2AuthenticationException("Email not verified by Google");
        }

        String baseUsername = email.substring(0, email.indexOf("@")).toLowerCase();
        String uniqueUsername = baseUsername;

        int counter = 1;

        while (userRepository.existsByUsername(uniqueUsername)) {
            uniqueUsername = baseUsername + counter;
            counter++;
        }

        String finalUniqueUsername = uniqueUsername;

        Supplier<User> userCreateSupplier = () -> userRepository.save(User.builder()
                .email(email)
                .username(finalUniqueUsername)
                .isVerified(true)
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.ACTIVE)
                .password(passwordEncoder.encode("1234"))
                .build()
        );

        User user = userRepository.findByEmail(email)
                .orElseGet(userCreateSupplier);

        return oidcUser;

    }

}
