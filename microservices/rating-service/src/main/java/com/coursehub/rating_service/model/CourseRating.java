package com.coursehub.rating_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "course_ratings",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "course_id"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(nullable = false)
    private double rating;

}
