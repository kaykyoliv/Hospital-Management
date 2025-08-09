package com.kayky.domain.user;

import com.kayky.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private UserRepository repository;

    public void assertEmailDoesNotExist(String email) {
        repository.findByEmail(email).ifPresent(this::throwEmailExistsException);
    }

    public void assertEmailDoesNotExist(String email, Long id) {
        repository.findByEmailAndIdNot(email, id).ifPresent(this::throwEmailExistsException);
    }

    public void throwEmailExistsException(User user) {
        log.warn("Email conflict: {} already in use by patient ID {}", user.getEmail(), user.getId());

        throw new EmailAlreadyExistsException("Email %s already in use".formatted(user.getEmail()));
    }
}
