package com.coursehub.course_service.model;

import com.coursehub.course_service.model.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import com.coursehub.commons.feign.enums.Currency;

import static com.coursehub.course_service.model.enums.CourseStatus.PENDING;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.UUID;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "courses")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Course {

    @Id
    @GeneratedValue(strategy = UUID)
    String id;

    String title;

    @Column(name = "TEXT", nullable = false, length = 1000)
    String description;

    String instructorId;

    BigDecimal price;

    @Enumerated(STRING)
    Currency currency;

    @Builder.Default
    @Enumerated(STRING)
    CourseStatus status = PENDING;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @Builder.Default
    Double rating = 0.0;

    @Builder.Default
    int ratingCount = 0;

    @Builder.Default
    @ManyToMany(fetch = LAZY, cascade = MERGE)
    @JoinTable(
            name = "course_categories",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    Set<Category> categories = new HashSet<>();


    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = ALL, fetch = LAZY)
    List<Video> videos = new ArrayList<>();

    @Column
    String posterPicture;

}