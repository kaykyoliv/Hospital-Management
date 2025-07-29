package com.kayky.domain.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/user")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to user management")
public class UserController {

    private final UserService service;

    @Operation(
            summary = "Activate user",
            description = "Activates a user account that is currently inactive")
    @ApiResponses(value = {
            @ApiResponse( responseCode = "204", description = "User activated successfully"),
            @ApiResponse( responseCode = "404", description = "User not found")

    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id){
        service.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user account that is currently activate")
    @ApiResponses(value = {
            @ApiResponse( responseCode = "204", description = "User deactivate successfully"),
            @ApiResponse( responseCode = "404", description = "User not found")

    })    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id){
        service.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

}
