package com.coursehub.identity_service.security;

import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        boolean emailVerified = Boolean.TRUE.equals(oidcUser.getEmailVerified());

        if (email == null || !emailVerified) {
            throw new OAuth2AuthenticationException("Email not verified by Google");
        }

        // burada veritabanı işlemi: user varsa getir, yoksa yarat
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .isVerified(true)
                        .build()));

        // geri dön: Spring Security kullanımı için gerekli dönüş
        return oidcUser;

    }
}
