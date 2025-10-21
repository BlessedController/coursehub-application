package com.coursehub.course_service.model;

import com.coursehub.course_service.model.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.coursehub.course_service.model.enums.CourseStatus.PENDING;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "COURSES")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 150)
    String title;

    @Column(name = "TEXT", nullable = false, length = 1000)
    String description;

    @Column(nullable = false)
    String instructorId;

    @Column(nullable = false)
    BigDecimal price;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(STRING)
    CourseStatus status = PENDING;

    @CreationTimestamp
    @Column(nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    Double rating = 0.0;

    @Builder.Default
    @Column(nullable = false)
    Long ratingCount = 0L;


    @Builder.Default
    @ManyToMany(fetch = LAZY, cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinTable(
            name = "course_categories",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    Set<Category> categories = new HashSet<>();

}