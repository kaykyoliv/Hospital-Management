package com.kayky.domain.user;

import com.kayky.domain.doctor.Doctor;
import com.kayky.domain.patient.Patient;
import com.kayky.exception.EmailAlreadyExistsException;
import com.kayky.exception.ResourceNotFoundException;
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
        return repository.findById(id)
                .map(Patient.class::cast)
                .orElseThrow(() -> {
                    log.warn("Patient with id {} not found", id);
                    return new ResourceNotFoundException("Patient with id %d not found".formatted(id));
                });
    }

    public Doctor getDoctorIfExists(Long id) {
        return repository.findById(id)
                .map(Doctor.class::cast)
                .orElseThrow(() -> {
                    log.warn("Doctor with id {} not found", id);
                    return new ResourceNotFoundException("Doctor with id %d not found".formatted(id));
                });
    }

}
