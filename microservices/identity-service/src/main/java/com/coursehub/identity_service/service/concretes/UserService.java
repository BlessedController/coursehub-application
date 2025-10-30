package com.coursehub.identity_service.service.concretes;

import com.coursehub.commons.exceptions.*;
import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.user.UserResponseForOthers;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.exception.*;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.UserRole;
import com.coursehub.identity_service.model.enums.UserStatus;
import com.coursehub.identity_service.repository.IUserRepository;
import com.coursehub.identity_service.security.IJwtService;
import com.coursehub.identity_service.security.UserPrincipal;
import com.coursehub.identity_service.service.absttracts.IEmailService;
import com.coursehub.identity_service.service.absttracts.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static com.coursehub.identity_service.constants.JwtTokenConstants.ACCESS_TOKEN_COOKIE;
import static com.coursehub.identity_service.mapper.UserMapper.*;
import static com.coursehub.identity_service.model.enums.UserRole.ROLE_INSTRUCTOR;
import static com.coursehub.identity_service.model.enums.UserRole.ROLE_USER;
import static com.coursehub.identity_service.model.enums.UserStatus.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.StringUtils.hasText;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    IUserRepository userRepository;
    PasswordEncoder passwordEncoder;
    IJwtService jwtService;
    AuthenticationProvider authenticationProvider;
    IEmailService emailService;

    @Override
    public boolean existsByEmail(String email) {
        log.info("Entering existsByEmail with email: {}", email);
        boolean result = userRepository.existsByEmail(email);
        log.info("Exiting existsByEmail -> {}", result);
        return result;
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("Entering existsByUsername with username: {}", username);
        boolean result = userRepository.existsByUsername(username);
        log.debug("Exiting existsByUsername -> {}", result);
        return result;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        log.debug("Entering existsByPhoneNumber with phoneNumber: {}", phoneNumber);
        boolean result = userRepository.existsByPhoneNumber(phoneNumber);
        log.debug("Exiting existsByPhoneNumber -> {}", result);
        return result;
    }

    @Override
    public void register(CreateUserRequest request) {
        log.info("Entering register with email: {}", request.email());
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        log.info("User registered successfully with id: {}", user.getId());
        log.debug("Exiting register");
    }

    @Override
    public UserResponseForOthers getUserById(String id) {
        log.info("Entering getUserById with id: {}", id);
        User targetUser = findUserByIdAndUserStatusInAndUserRoleIn(id, Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        UserResponseForOthers response = buildUserResponseForOthers(targetUser);
        log.debug("Exiting getUserById for id: {}", id);
        return response;
    }

    @Override
    public void updateSelfInfo(UserPrincipal principal, UpdateUserInfoRequest request) {
        log.info("Entering updateSelfInfo for userId: {}", principal.getId());
        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        updateUser(currentUser, request);
        userRepository.save(currentUser);
        log.info("Self info updated for userId: {}", currentUser.getId());
        log.debug("Exiting updateSelfInfo");
    }

    @Override
    public void updateSelfPrincipals(UserPrincipal principal, UpdateSelfPrincipalsRequest request) {
        log.info("Entering updateSelfPrincipals for userId: {}", principal.getId());
        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        currentUser.setUsername(request.username() == null ? currentUser.getUsername() : request.username());
        currentUser.setEmail(request.email() == null ? currentUser.getEmail() : request.email());
        currentUser.setPhoneNumber(request.phoneNumber() == null ? currentUser.getPhoneNumber() : request.phoneNumber());
        userRepository.save(currentUser);
        log.info("Principals updated for userId: {}", currentUser.getId());
        log.debug("Exiting updateSelfPrincipals");
    }

    @Override
    public void updateSelfPassword(UserPrincipal principal, UpdateUserPasswordRequest request) {
        log.info("Entering updateSelfPassword for userId: {}", principal.getId());
        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        currentUser.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(currentUser);
        log.info("Password updated for userId: {}", currentUser.getId());
        log.debug("Exiting updateSelfPassword");
    }

    @Override
    public void inactivateSelf(UserPrincipal principal) {
        log.info("Entering inactivateSelf for userId: {}", principal.getId());
        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        currentUser.setUserStatus(INACTIVE);
        userRepository.save(currentUser);
        log.info("User inactivated with id: {}", currentUser.getId());
        log.debug("Exiting inactivateSelf");
    }

    @Override
    public UserSelfResponse getSelf(UserPrincipal principal) {
        log.info("Entering getSelf for userId: {}", principal.getId());
        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        UserSelfResponse response = buildUserSelfResponse(currentUser);
        log.debug("Exiting getSelf for userId: {}", principal.getId());
        return response;
    }

    @Override
    public void deleteSelf(UserPrincipal principal) {
        log.warn("Entering deleteSelf for userId: {}", principal.getId());
        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        currentUser.setUserStatus(DELETED);
        userRepository.save(currentUser);
        log.info("User deleted (soft) with id: {}", currentUser.getId());
        log.debug("Exiting deleteSelf");
    }

    @Override
    public String authenticate(LoginRequest request, HttpServletResponse response) {
        log.info("Entering authenticate for identifier: {}", request.identifier());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(request.identifier(), request.password());
        Authentication authentication = authenticationProvider.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(principal);
        createCookie(accessToken, null, response);

        log.info("Authentication successful for userId: {}", principal.getId());
        log.debug("Exiting authenticate for userId: {}", principal.getId());
        return accessToken;
    }

    @Override
    public void getVerifyMail(UserPrincipal principal) {
        log.info("Entering getVerifyMail for userId: {}", principal.getId());
        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));
        if (TRUE.equals(currentUser.getIsVerified())) {
            log.warn("User {} already verified, cannot send mail", currentUser.getId());
            throw new InvalidRequestException("User is already verified");
        }
        emailService.sendVerifyingEmail(currentUser.getEmail(), currentUser.getActivationCode());
        log.info("Verify mail sent to: {}", currentUser.getEmail());
        log.debug("Exiting getVerifyMail for userId: {}", principal.getId());
    }

    @Override
    public void verify(UserPrincipal principal, String activationCode) {
        log.info("Entering verify for userId: {} with code: {}", principal.getId(), activationCode);

        User userByActivationCode = userRepository.findByActivationCode(activationCode)
                .orElseThrow(() -> {
                    log.error("No user found with activationCode: {}", activationCode);
                    return new NotFoundException("User with activation code " + activationCode + " not found");
                });

        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));

        if (!userByActivationCode.getId().equals(currentUser.getId())) {
            log.error("Invalid activation code {} for userId {}", activationCode, principal.getId());
            throw new InvalidRequestException("Invalid activation code");
        }

        currentUser.setIsVerified(TRUE);

        currentUser.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(currentUser);
        log.info("User verified successfully: {}", currentUser.getId());
        log.debug("Exiting verify for userId: {}", principal.getId());
    }

    @Override
    public UserSelfResponse becameInstructor(UserPrincipal principal) {
        log.info("Entering becameInstructor for userId: {}", principal.getId());

        User currentUser = findUserByIdAndUserStatusInAndUserRoleIn(principal.getId(), Set.of(ACTIVE, INACTIVE), Set.of(ROLE_USER, ROLE_INSTRUCTOR));

        if (!TRUE.equals(currentUser.getIsVerified())) {
            log.error("User {} is not verified, cannot become instructor", currentUser.getId());
            throw new UserNotVerifiedException("User must be verified to become instructor");
        }

        if (!ACTIVE.equals(currentUser.getUserStatus())) {
            log.error("User {} is not active, cannot become instructor", currentUser.getId());
            throw new UserNotActiveException("Your account is not active");
        }


        if (ROLE_INSTRUCTOR.equals(currentUser.getUserRole())) {
            log.info("User {} is already instructor", currentUser.getId());
            return buildUserSelfResponse(currentUser);
        }

        validatePersonalInfo(currentUser);

        currentUser.setUserRole(ROLE_INSTRUCTOR);

        User savedUser = userRepository.save(currentUser);

        log.info("User {} became instructor successfully", savedUser.getId());
        log.debug("Exiting becameInstructor for userId: {}", principal.getId());

        return buildUserSelfResponse(savedUser);
    }


    @Override
    public Boolean isInstructorExist(String instructorId) {
        if (!hasText(instructorId)) return FALSE;

        return userRepository.findById(instructorId)
                .map(user -> ROLE_INSTRUCTOR.equals(user.getUserRole()) && ACTIVE.equals(user.getUserStatus()))
                .orElse(FALSE);
    }


    @Override
    public User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles) {
        return userRepository.findByIdAndUserStatusInAndUserRoleIn(id, statuses, userRoles)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void createCookie(String accessToken, String refreshToken, HttpServletResponse response) {
        log.debug("Entering createCookie");
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE, accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);
        response.addCookie(accessCookie);
        Cookie refreshNewCookie = new Cookie("refresh_token", refreshToken);
        refreshNewCookie.setHttpOnly(true);
        refreshNewCookie.setSecure(true);
        refreshNewCookie.setPath("/v1/user/refresh");
        refreshNewCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshNewCookie);
        log.debug("Exiting createCookie");
    }

    private void validatePersonalInfo(User user) {
        if (!hasText(user.getFirstName()) ||
                !hasText(user.getLastName()) ||
                !hasText(user.getPhoneNumber()) ||
                user.getGender() == null) {
            log.error("User {} missing personal info, cannot become instructor", user.getId());
            throw new InvalidRequestException("Some required fields are missing");
        }
    }


}
