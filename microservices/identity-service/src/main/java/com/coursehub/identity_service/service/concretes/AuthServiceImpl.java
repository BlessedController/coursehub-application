package com.coursehub.identity_service.service.concretes;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.security.*;
import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.common.TokenResponse;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import com.coursehub.identity_service.service.abstracts.AuthService;
import com.coursehub.identity_service.service.abstracts.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

import static com.coursehub.commons.security.JwtService.*;
import static com.coursehub.commons.security.UserRole.ROLE_CONTENT_CREATOR;
import static com.coursehub.commons.security.UserRole.ROLE_USER;
import static com.coursehub.commons.security.UserStatus.*;
import static com.coursehub.identity_service.mapper.UserMapper.toUserSelfResponse;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationProvider authenticationProvider;
    private final EmailService emailService;


    @Override
    public void register(CreateUserRequest request) {
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
    }


    @Override
    public TokenResponse authenticate(LoginRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(request.identifier(), request.password());
        Authentication authentication = authenticationProvider.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        this.createCookie(accessToken, refreshToken, response);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse refresh(RefreshTokenRequest request, HttpServletResponse response) {

        Optional<String> subject = jwtService.verifyRefreshToken(request.refreshToken());

        if (subject.isEmpty()) {
            throw new InvalidRequestException("Invalid or expired refresh token");
        }

        User user = userRepository.findByUsername(subject.get())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole()
        );

        String newAccessToken = jwtService.generateAccessToken(principal);

        this.createCookie(newAccessToken, request.refreshToken(), response);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.refreshToken())
                .build();
    }

    @Override
    public void logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Cookie clearAccess = new Cookie(ACCESS_TOKEN_COOKIE, null);
        clearAccess.setMaxAge(0);
        clearAccess.setPath("/");
        response.addCookie(clearAccess);

        Cookie clearRefresh = new Cookie(REFRESH_TOKEN_COOKIE, null);
        clearRefresh.setMaxAge(0);
        clearRefresh.setPath("/v1/users/auth/refresh");
        response.addCookie(clearRefresh);
    }

    @Override
    public UserSelfResponse becameInstructor(UserPrincipal principal) {
        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR));

        if (!TRUE.equals(currentUser.getIsVerified())) {
            throw new UserNotVerifiedException("User must be verified to become instructor");
        }

        if (!ACTIVE.equals(currentUser.getUserStatus())) {
            throw new UserNotActiveException("Your account is not active");
        }


        if (ROLE_CONTENT_CREATOR.equals(currentUser.getUserRole())) {
            return toUserSelfResponse(currentUser);
        }

        this.validatePersonalInfo(currentUser);

        currentUser.setUserRole(ROLE_CONTENT_CREATOR);

        User savedUser = userRepository.save(currentUser);

        return toUserSelfResponse(savedUser);
    }


    @Override
    public Boolean isContentCreatorExist(String instructorId) {
        if (!hasText(instructorId)) return FALSE;

        return userRepository.findById(instructorId)
                .map(user -> ROLE_CONTENT_CREATOR.equals(user.getUserRole()) && ACTIVE.equals(user.getUserStatus()))
                .orElse(FALSE);
    }

    @Override
    public void getVerifyMail(UserPrincipal principal) {
        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR));
        if (TRUE.equals(currentUser.getIsVerified())) {
            throw new InvalidRequestException("User is already verified");
        }
        emailService.sendVerifyingEmail(currentUser.getEmail(), currentUser.getActivationCode());
    }

    @Override
    public void verify(UserPrincipal principal, String activationCode) {
        User userByActivationCode = userRepository.findByActivationCode(activationCode)
                .orElseThrow(() -> new NotFoundException("User with activation code " + activationCode + " not found"));

        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR));

        if (!userByActivationCode.getId().equals(currentUser.getId())) {
            throw new InvalidRequestException("Invalid activation code");
        }

        currentUser.setIsVerified(TRUE);

        currentUser.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(currentUser);
    }

    @Override
    public void deleteSelf(UserPrincipal principal) {
        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR));
        currentUser.setUserStatus(DELETED);
        userRepository.save(currentUser);
    }

    @Override
    public void updateSelfPassword(UserPrincipal principal, UpdateUserPasswordRequest request) {
        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR));
        currentUser.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(currentUser);
    }

    @Override
    public void inactivateSelf(UserPrincipal principal) {
        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(
                principal.getId(),
                Set.of(ACTIVE),
                Set.of(ROLE_USER, ROLE_CONTENT_CREATOR)
        );
        currentUser.setUserStatus(SUSPENDED);
        userRepository.save(currentUser);
    }

    @Override
    public void updateSelfPrincipals(UserPrincipal principal, UpdateSelfPrincipalsRequest request) {
        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR));
        currentUser.setUsername(request.username() == null ? currentUser.getUsername() : request.username());
        currentUser.setEmail(request.email() == null ? currentUser.getEmail() : request.email());
        currentUser.setPhoneNumber(request.phoneNumber() == null ? currentUser.getPhoneNumber() : request.phoneNumber());
        userRepository.save(currentUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }


    @Override
    public User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles) {
        return userRepository.findByIdAndUserStatusInAndUserRoleIn(id, statuses, userRoles)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void createCookie(String accessToken, String refreshToken, HttpServletResponse response) {
        // ACCESS TOKEN COOKIE
        ResponseCookie accessCookie = ResponseCookie.from(JwtService.ACCESS_TOKEN_COOKIE, accessToken)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(ACCESS_TOKEN_EXPIRATION))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        // REFRESH TOKEN COOKIE
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .path("/v1/auth/refresh")
                .maxAge(Duration.ofMillis(REFRESH_TOKEN_EXPIRATION))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void validatePersonalInfo(User user) {
        if (!hasText(user.getFirstName()) ||
                !hasText(user.getLastName()) ||
                !hasText(user.getPhoneNumber()) ||
                user.getGender() == null) {
            throw new InvalidRequestException("Some required fields are missing");
        }
    }
}
