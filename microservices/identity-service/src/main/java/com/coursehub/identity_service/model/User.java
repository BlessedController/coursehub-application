package com.coursehub.identity_service.model;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static com.coursehub.commons.security.model.UserRole.ROLE_USER;
import static com.coursehub.commons.security.model.UserStatus.ACTIVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.UUID;
import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = PRIVATE)
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = UUID)
    String id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false, unique = true)
    String email;

    @Column(unique = true, length = 30)
    String phoneNumber;

    @Column(nullable = false)
    String password;

    @Column
    String firstName;

    @Column(length = 50)
    String lastName;

    @Column(unique = true)
    @Builder.Default
    String activationCode = java.util.UUID.randomUUID().toString();

    @Column
    String tempCode;

    @Column
    LocalDateTime tempCodeExpiresAt;

    @Column
    @Enumerated(STRING)
    Gender gender;

    @Column
    String aboutMe;

    @Column
    @Builder.Default
    Double rating = 0.0;

    @Column
    @Builder.Default
    int ratingCount = 0;

    @Enumerated(STRING)
    @Builder.Default
    @Column(nullable = false)
    UserRole userRole = ROLE_USER;

    @Enumerated(STRING)
    @Builder.Default
    @Column(nullable = false)
    UserStatus userStatus = ACTIVE;

    @Column
    @Builder.Default
    Boolean isVerified = FALSE;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Column
    String profilePhotoName;

}