package com.coursehub.course_service.model;

import com.coursehub.course_service.model.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

import static com.coursehub.course_service.model.enums.CategoryStatus.INACTIVE;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    @Enumerated(STRING)
    @Builder.Default
    CategoryStatus status = INACTIVE;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    Category parentCategory;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    List<Course> courses = new ArrayList<>();

}
