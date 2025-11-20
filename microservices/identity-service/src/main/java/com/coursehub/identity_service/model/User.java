package com.coursehub.identity_service.model;

import com.coursehub.commons.security.UserRole;
import com.coursehub.commons.security.UserStatus;
import com.coursehub.identity_service.model.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static com.coursehub.commons.security.UserRole.ROLE_USER;
import static com.coursehub.commons.security.UserStatus.ACTIVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.UUID;
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
    @Enumerated(STRING)
    Gender gender;

    @Column
    String aboutMe;

    @Column
    @Builder.Default
    Double rating = 0.0;

    @Enumerated(STRING)
    @Builder.Default
    @Column(nullable = false)
    UserRole userRole = ROLE_USER;

    @Enumerated(STRING)
    @Builder.Default
    @Column(nullable = false)
    UserStatus userStatus = ACTIVE;

    @Column(nullable = false)
    Boolean isVerified;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", activationCode='" + activationCode + '\'' +
                ", gender=" + gender +
                ", aboutMe='" + aboutMe + '\'' +
                ", rating=" + rating +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userStatus=" + userStatus +
                ", userRole=" + userRole +
                '}';
    }
}