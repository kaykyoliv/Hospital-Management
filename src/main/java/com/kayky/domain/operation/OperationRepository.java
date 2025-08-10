package com.kayky.domain.operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}