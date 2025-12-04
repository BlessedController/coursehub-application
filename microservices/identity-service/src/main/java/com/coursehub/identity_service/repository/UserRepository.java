package com.coursehub.identity_service.repository;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, String> , JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByActivationCode(String activationCode);

    Optional<User> findByIdAndUserStatusInAndUserRoleIn(String id, Collection<UserStatus> userStatuses, Collection<UserRole> userRoles);

    @Query("""
    SELECT u FROM User u
    WHERE u.userRole = com.coursehub.commons.security.model.UserRole.ROLE_CONTENT_CREATOR
    AND (:rating IS NULL OR u.rating >= :rating)
    AND (
            LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR (u.aboutMe IS NOT NULL AND LOWER(u.aboutMe) LIKE LOWER(CONCAT('%', :keyword, '%')))
    )
    """)
    Page<User> filterContentCreators(
            @Param("rating") Double rating,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    Optional<User> findByEmailAndUserStatus(String email, UserStatus userStatus);
}
