package com.kayky.domain.operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    @Query("""
            SELECT
                o.id as id,
                o.description as description,
                o.scheduledAt as scheduledAt,
                d.firstName as doctorFirstName,
                p.firstName as patientFirstName,
                o.status as status
            FROM Operation o
            JOIN o.doctor as d
            JOIN o.patient as p
            """)
    Page<OperationProjection> findAllProjected(Pageable pageable);

    @EntityGraph(attributePaths = {"doctor", "patient"})
    Optional<Operation> findById(Long id);
}