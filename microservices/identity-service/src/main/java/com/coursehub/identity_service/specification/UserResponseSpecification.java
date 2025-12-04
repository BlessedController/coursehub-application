package com.coursehub.identity_service.specification;

import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public class UserResponseSpecification {

    public static Specification<User> filter(Collection<UserStatus> statuses, List<String> ids) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(
                        root.get("userStatus").in(statuses)
                );
            }

            if (ids != null && !ids.isEmpty()) {
                predicates.add(
                        root.get("id").in(ids)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
