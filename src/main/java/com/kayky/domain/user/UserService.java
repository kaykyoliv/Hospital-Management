package com.kayky.domain.user;

import com.kayky.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void activateUser(Long id){
        var user = findUserById(id);

        validateUserState(user, false, "activate");

        user.setActive(true);
        log.info("Activated user ID: {}", id);
    }

    @Transactional
    public void deactivateUser(Long id){
        var user = findUserById(id);

        validateUserState(user, true, "deactivate");

        user.setActive(false);
        log.info("Deactivated user ID: {}", id);
    }

    @Transactional(readOnly = true)
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found");
                });
    }

    private void validateUserState(User user, boolean expectedState, String action){
        if(user.getActive() != expectedState){
            String currentState = user.getActive() ? "active" : "inactive";

            log.warn("Attempted to {} user ID {}, but user is already {}",
                    action, user.getId(), currentState);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("User already %s", currentState));
        }
    }
}
