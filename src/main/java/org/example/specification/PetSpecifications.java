package org.example.specification;

import org.example.entity.Color;
import org.example.entity.Pet;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PetSpecifications {

    public static Specification<Pet> hasColor(Color color) {
        return (root, query, cb) -> {
            if (color == null) return cb.conjunction();
            return cb.equal(root.get("color"), color);
        };
    }

    public static Specification<Pet> hasBreed(String breed) {
        return (root, query, cb) -> {
            if (breed == null || breed.isEmpty()) return cb.conjunction();
            return cb.like(root.get("breed"), "%" + breed + "%");
        };
    }

    public static Specification<Pet> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Pet> hasBirthday(LocalDate birthday) {
        return (root, query, cb) -> {
            if (birthday == null) return cb.conjunction();
            return cb.equal(root.get("birthday"), birthday);
        };
    }

    public static Specification<Pet> birthdayBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null || end == null) return cb.conjunction();
            return cb.between(root.get("birthday"), start, end);
        };
    }

}