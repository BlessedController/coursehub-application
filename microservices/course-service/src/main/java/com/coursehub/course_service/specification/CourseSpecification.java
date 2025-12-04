package com.coursehub.course_service.specification;

import com.coursehub.course_service.dto.request.CourseFilterRequest;
import com.coursehub.course_service.model.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {

    public static Specification<Course> filter(CourseFilterRequest f) {
        return (root, query, cb) -> {

            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (f.status() != null)
                predicates.add(cb.equal(root.get("status"), f.status()));

            if (f.keyword() != null)
                predicates.add(cb.like(cb.lower(root.get("title")),
                        "%" + f.keyword().toLowerCase() + "%"));

            if (f.minPrice() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), f.minPrice()));

            if (f.maxPrice() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), f.maxPrice()));

            if (f.minTime() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.minTime()));

            if (f.maxTime() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.maxTime()));

            if (f.minRating() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), f.minRating()));

            if (f.maxRating() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), f.maxRating()));

            if (f.category() != null)
                predicates.add(cb.equal(root.join("categories").get("id"), f.category()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

