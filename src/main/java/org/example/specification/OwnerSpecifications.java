package org.example.specification;

import org.example.entity.Owner;
import org.example.entity.Pet;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class OwnerSpecifications {
    public static Specification<Owner> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }

    public static Specification<Owner> hasBirthday(LocalDate birthday) {
        return (root, query, cb) -> {
            if (birthday == null) return cb.conjunction();
            return cb.equal(root.get("dateOfBirth"), birthday);
        };
    }

    public static Specification<Pet> birthdayBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null || end == null) return cb.conjunction();
            return cb.between(root.get("birthday"), start, end);
        };
    }
}