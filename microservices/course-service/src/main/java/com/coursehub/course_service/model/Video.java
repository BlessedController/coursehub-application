package com.coursehub.course_service.model;

import com.coursehub.course_service.model.enums.VideoStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static com.coursehub.course_service.model.enums.VideoStatus.PENDING;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.UUID;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "videos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Video {

    @Id
    @GeneratedValue(strategy = UUID)
    String id;

    String displayName;

    @Column(nullable = false, unique = true)
    String videoPath;

    @Enumerated(STRING)
    @Column(nullable = false)
    @Builder.Default
    VideoStatus status = PENDING;

    @ManyToOne(fetch = LAZY)
    Course course;


}
