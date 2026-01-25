package com.kayky.domain.user;

import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.doctor.Doctor;
import com.kayky.domain.doctor.DoctorRepository;
import com.kayky.domain.patient.Patient;
import com.kayky.domain.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;  // novo
    private final DoctorRepository doctorRepository;    // novo

    public void assertEmailDoesNotExist(String email) {
        userRepository.findByEmail(email).ifPresent(this::throwEmailExistsException);
    }

    public void assertEmailDoesNotExist(String email, Long id) {
        userRepository.findByEmailAndIdNot(email, id).ifPresent(this::throwEmailExistsException);
    }

    public void throwEmailExistsException(User user) {
        log.warn("Email conflict: {} already in use by user ID {}", user.getEmail(), user.getId());

        throw new EmailAlreadyExistsException("Email %s already in use".formatted(user.getEmail()));
    }

    public void assertIfUserExist(Long id, String userType) {
        // Pode manter genérico, mas agora com repos específicos
        if ("Patient".equals(userType)) {
            getPatientIfExists(id);  // lança exceção se não existir
        } else if ("Doctor".equals(userType)) {
            getDoctorIfExists(id);
        } else {
            userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(userType + " with id " + id + " not found"));
        }
    }

    public Patient getPatientIfExists(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient with id {} not found", id);
                    return new ResourceNotFoundException("Patient with id %d not found".formatted(id));
                });
    }

    public Doctor getDoctorIfExists(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Doctor with id {} not found", id);
                    return new ResourceNotFoundException("Doctor with id %d not found".formatted(id));
                });
    }

}
