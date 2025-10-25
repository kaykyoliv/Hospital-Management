package com.kayky.domain.user;

import com.kayky.domain.doctor.Doctor;
import com.kayky.domain.patient.Patient;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private final UserRepository repository;

    public void assertEmailDoesNotExist(String email) {
        repository.findByEmail(email).ifPresent(this::throwEmailExistsException);
    }

    public void assertEmailDoesNotExist(String email, Long id) {
        repository.findByEmailAndIdNot(email, id).ifPresent(this::throwEmailExistsException);
    }

    public void throwEmailExistsException(User user) {
        log.warn("Email conflict: {} already in use by user ID {}", user.getEmail(), user.getId());

        throw new EmailAlreadyExistsException("Email %s already in use".formatted(user.getEmail()));
    }

    public void assertIfUserExist(Long id, String userType){
        if (!repository.existsById(id)) {
            log.warn("{} with id {} not found", userType, id);
            throw new ResourceNotFoundException("%s with id %d not found".formatted(userType, id));
        }
    }

    public Patient getPatientIfExists(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient with id {} not found", id);
                    return new ResourceNotFoundException("Patient with id %d not found".formatted(id));
                });

        if (!(user instanceof Patient patient)) {
            log.warn("User with id {} is not a Patient", id);
            throw new IllegalArgumentException("ID %d does not belong to a Patient".formatted(id));
        }

        return patient;
    }

    public Doctor getDoctorIfExists(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Doctor with id {} not found", id);
                    return new ResourceNotFoundException("Doctor with id %d not found".formatted(id));
                });

        if (!(user instanceof Doctor doctor)) {
            log.warn("User with id {} is not a Doctor", id);
            throw new IllegalArgumentException("ID %d does not belong to a Doctor".formatted(id));
        }

        return doctor;
    }

}
