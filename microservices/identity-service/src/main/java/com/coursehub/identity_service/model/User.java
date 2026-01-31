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
    @GeneratedValue(strategy = UUID)
    String id;

    String username;

    String email;

    String phoneNumber;

    String password;

    String firstName;

    String lastName;

    String activationCode;

    String tempCode;

    LocalDateTime tempCodeExpiresAt;

    @Enumerated(EnumType.STRING)
    Gender gender;

    String aboutMe;

    @Builder.Default
    Double rating = 0.0;

    @Builder.Default
    int ratingCount = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    UserRole userRole = ROLE_USER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    UserStatus userStatus = ACTIVE;

    @Builder.Default
    Boolean isVerified = FALSE;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    String profilePictureName;

}