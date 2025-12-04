package com.coursehub.identity_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.security.model.*;
import com.coursehub.commons.security.service.JwtUserAccessTokenService;
import com.coursehub.commons.security.service.JwtUserRefreshTokenService;
import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.common.TokenResponse;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.mapper.UserMapper;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import com.coursehub.identity_service.service.AuthService;
import com.coursehub.identity_service.service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.coursehub.commons.security.constants.JwtConstants.ACCESS_TOKEN_COOKIE;
import static com.coursehub.commons.security.constants.JwtConstants.REFRESH_TOKEN_COOKIE;
import static com.coursehub.commons.security.model.UserRole.*;
import static com.coursehub.commons.security.model.UserStatus.*;
import static com.coursehub.identity_service.constants.JwtTokenConstants.ACCESS_TOKEN_EXPIRATION;
import static com.coursehub.identity_service.constants.JwtTokenConstants.REFRESH_TOKEN_EXPIRATION;
import static java.lang.Boolean.TRUE;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUserAccessTokenService jwtServiceUserAccessToken;
    private final JwtUserRefreshTokenService jwtServiceUserRefreshToken;
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
    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(request.identifier(), request.password());
        Authentication authentication = authenticationProvider.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtServiceUserAccessToken.generateAccessToken(principal, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtServiceUserRefreshToken.generateRefreshToken(principal, REFRESH_TOKEN_EXPIRATION);

        this.createCookie(accessToken, refreshToken, response);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse refresh(RefreshTokenRequest request, HttpServletResponse response) {

        Optional<String> subject = jwtServiceUserRefreshToken.verifyRefreshToken(request.refreshToken());

        if (subject.isEmpty()) {
            throw new InvalidRequestException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(subject.get())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().name()
        );

        String newAccessToken = jwtServiceUserAccessToken.generateAccessToken(principal, ACCESS_TOKEN_EXPIRATION);

        this.createCookie(newAccessToken, request.refreshToken(), response);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.refreshToken())
                .build();
    }

    @Override
    public void logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        this.clearCookies(response);
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
            return UserMapper.toUserSelfResponse(currentUser);
        }

        this.validatePersonalInfo(currentUser);

        currentUser.setUserRole(ROLE_CONTENT_CREATOR);

        User savedUser = userRepository.save(currentUser);

        return UserMapper.toUserSelfResponse(savedUser);
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
    public void resetPassword(UserPrincipal principal, UpdateUserPasswordRequest request) {

        if (!request.password().equals(request.passwordTheSecond())) {
            throw new InvalidRequestException("Passwords do not match");
        }

        User currentUser = this.findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR, ROLE_ADMIN));

        if (!StringUtils.hasText(request.oldPassword()) ||
                !passwordEncoder.matches(request.oldPassword(), currentUser.getPassword())) {
            throw new InvalidRequestException("Invalid old password");
        }

        currentUser.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(currentUser);
    }

    @Override
    public void sendResetPasswordCode(SendResetPasswordCodeRequest request) {

        User user = userRepository.findByEmailAndUserStatus(request.email(), UserStatus.ACTIVE).orElse(null);

        if (user == null) return;

        String tempResetCode = UUID.randomUUID().toString();

        user.setTempCode(tempResetCode);

        user.setTempCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);

        emailService.sendEmailToResetPassword(request.email(), tempResetCode);
    }

    @Override
    public void resetForgottenPassword(ForgottenPasswordResetRequest request) {

        User user = userRepository.findByEmailAndUserStatus(request.email(), UserStatus.ACTIVE).orElse(null);

        if (user == null) return;

        if (!request.resetCode().equals(user.getTempCode())) {
            throw new InvalidRequestException("Invalid reset code");
        }

        if (user.getTempCodeExpiresAt() == null ||
                user.getTempCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Expired reset code");
        }

        if (!request.password().equals(request.passwordTheSecond())) {
            throw new InvalidRequestException("Passwords do not match");
        }

        user.setTempCode(null);
        user.setTempCodeExpiresAt(null);

        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

    }

    @Override
    public void suspendSelf(UserPrincipal principal) {
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
    public User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles) {
        return userRepository.findByIdAndUserStatusInAndUserRoleIn(id, statuses, userRoles)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void createCookie(String accessToken, String refreshToken, HttpServletResponse response) {
        // ACCESS TOKEN COOKIE
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, accessToken)
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
                .path("/v1/api/users/auth/refresh")
                .maxAge(Duration.ofMillis(REFRESH_TOKEN_EXPIRATION))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    //todo: when frontend is ready cookie settings must be changed
    private void clearCookies(HttpServletResponse response) {

        // ACCESS TOKEN COOKIE DELETE
        ResponseCookie accessDelete = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/") // aynı path ile overwrite edilmeli
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessDelete.toString());

        // REFRESH TOKEN COOKIE DELETE
        ResponseCookie refreshDelete = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/v1/api/users/auth/refresh")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshDelete.toString());
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
