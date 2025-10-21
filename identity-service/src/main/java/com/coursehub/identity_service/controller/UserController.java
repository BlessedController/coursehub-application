package com.coursehub.identity_service.controller;

import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.GlobalSuccessMessage;
import com.coursehub.identity_service.dto.response.user.UserResponseForOthers;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.security.UserPrincipal;
import com.coursehub.identity_service.service.absttracts.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseForOthers> getUserById(@PathVariable(name = "id") String id) {
        return ok(userService.getUserById(id));
    }

    @GetMapping("/self")
    public ResponseEntity<UserSelfResponse> getSelf(@AuthenticationPrincipal UserPrincipal principal) {
        return ok(userService.getSelf(principal));
    }

    @PutMapping("/update")
    public ResponseEntity<GlobalSuccessMessage> updateSelfInfo(@AuthenticationPrincipal UserPrincipal principal,
                                                               @Valid @RequestBody UpdateUserInfoRequest request) {
        userService.updateSelfInfo(principal, request);
        return ok(new GlobalSuccessMessage("User info successfully updated"));
    }

    @PutMapping("/update-username")
    public ResponseEntity<GlobalSuccessMessage> updateSelfPrincipals(@AuthenticationPrincipal UserPrincipal principal,
                                                                     @Valid @RequestBody UpdateSelfPrincipalsRequest request
    ) {
        userService.updateSelfPrincipals(principal, request);
        return ok(new GlobalSuccessMessage("Successfully updated user principals"));
    }

    @PutMapping("/update-password")
    public ResponseEntity<GlobalSuccessMessage> updateSelfPassword(@AuthenticationPrincipal UserPrincipal principal,
                                                                   @Valid @RequestBody UpdateUserPasswordRequest request) {
        userService.updateSelfPassword(principal, request);
        return ok(new GlobalSuccessMessage("Successfully updated password"));
    }

    @PatchMapping("/inactive")
    public ResponseEntity<GlobalSuccessMessage> inactivateSelf(@AuthenticationPrincipal UserPrincipal principal) {
        userService.inactivateSelf(principal);
        return ok(new GlobalSuccessMessage("Account successfully inactivated"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GlobalSuccessMessage> deleteSelf(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteSelf(principal);
        return ok(new GlobalSuccessMessage("User deleted successfully"));
    }

    @GetMapping("/get-verifying-email")
    public ResponseEntity<Void> getVerifyMail(@AuthenticationPrincipal UserPrincipal principal) {
        userService.getVerifyMail(principal);
        return noContent().build();
    }

    @PatchMapping("/verify/{activation-code}")
    public ResponseEntity<Void> verify(@AuthenticationPrincipal UserPrincipal principal, @PathVariable(name = "activation-code") String activationCode) {
        userService.verify(principal, activationCode);
        return noContent().build();
    }

    @PutMapping("/became-instructor")
    public ResponseEntity<UserSelfResponse> becameInstructor(@AuthenticationPrincipal UserPrincipal principal) {

        UserSelfResponse response = userService.becameInstructor(principal);

        return status(HttpStatus.OK).body(response);
    }

    @GetMapping("/is-exist/{instructorId}")
    ResponseEntity<Boolean> isInstructorExist(@PathVariable String instructorId){
       Boolean isExist = userService.isInstructorExist(instructorId);

        return status(OK).body(isExist);
    }

}

