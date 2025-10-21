package com.coursehub.identity_service.repository;

import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.UserRole;
import com.coursehub.identity_service.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByActivationCode(String activationCode);

    Optional<User> findByIdAndUserStatusInAndUserRoleIn(String id, Collection<UserStatus> userStatuses, Collection<UserRole> userRoles);

}
