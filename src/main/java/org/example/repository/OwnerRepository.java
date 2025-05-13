package org.example.repository;

import org.example.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OwnerRepository extends
        JpaRepository<Owner, Long>,
        JpaSpecificationExecutor<Owner> {
}
