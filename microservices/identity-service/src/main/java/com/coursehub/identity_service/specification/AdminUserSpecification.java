package com.coursehub.identity_service.specification;

import com.coursehub.identity_service.dto.AdminUserSpecFilterRequest;
import com.coursehub.identity_service.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminUserSpecification {
    public static Specification<User> filter(AdminUserSpecFilterRequest f) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String keyword = f.keyword() == null ? "" : f.keyword();

            String like = "%" + keyword.toLowerCase() + "%";

            Predicate or = cb.or(
                    cb.like(cb.lower(root.get("username")), like),
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like)
            );

            predicates.add(or);

            if (f.status() != null)
                predicates.add(cb.equal(root.get("userStatus"), f.status()));

            if (f.role() != null)
                predicates.add(cb.equal(root.get("userRole"), f.role()));

            if (f.gender() != null)
                predicates.add(cb.equal(root.get("gender"), f.gender()));

            if (f.isVerified() != null)
                predicates.add(cb.equal(root.get("isVerified"), f.isVerified()));

            if (f.rating() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), f.rating()));

            if (f.minDate() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.minDate()));

            if (f.maxDate() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.maxDate()));


            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }
}
