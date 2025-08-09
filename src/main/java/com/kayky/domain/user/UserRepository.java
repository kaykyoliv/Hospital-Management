package com.kayky.domain.user;

import com.kayky.domain.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByEmailAndIdNot(String email, Long id);
}
