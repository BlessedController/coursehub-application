package com.coursehub.rating_service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"courseId", "userId"}))

public class CourseRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    String courseId;

    @Column(nullable = false)
    String userId;

    @Column(nullable = false)
    @Builder.Default
    Double rating = 0.0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime ratedAt;

    @UpdateTimestamp
//    @Column(nullable = false)
    LocalDateTime updatedAt;

}
