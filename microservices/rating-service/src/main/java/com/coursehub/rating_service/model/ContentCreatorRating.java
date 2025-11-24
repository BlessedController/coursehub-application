package com.coursehub.rating_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "content_creator_ratings",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "content_creator_id"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentCreatorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "content_creator_id", nullable = false)
    private String contentCreatorId;

    @Column(nullable = false)
    private double rating;

}
