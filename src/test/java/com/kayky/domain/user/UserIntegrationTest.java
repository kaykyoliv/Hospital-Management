package com.kayky.domain.user;

import com.kayky.config.BaseIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static com.kayky.commons.FileUtils.readResourceFile;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


@Sql(value = "/user/sql/cleanup-user-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/user/sql/user-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("User Controller - Integration Tests")
public class UserIntegrationTest extends BaseIntegrationTest {

    private static final Long ACTIVE_USER_ID = 1L;
    private static final Long INACTIVE_USER_ID = 3L;
    private static final Long NON_EXISTENT_ID = 999L;

    @Autowired
    private UserRepository userRepository;

    private ApiClient api() {
        return new ApiClient(port);
    }

    @Nested
    @DisplayName("PATCH /v1/user/{id}/activate")
    class ActivateUserEndpoint {
        @Test
        @DisplayName("Should return 204 and activate user when user exists and is inactive")
        void shouldActivateUser_andReturn204() {
            api().patch("/{id}/activate", HttpStatus.NO_CONTENT, Map.of("id", INACTIVE_USER_ID));

            User user = userRepository.findById(INACTIVE_USER_ID).orElseThrow();
            assertThat(user.getActive()).isTrue();
        }

        @Test
        @DisplayName("Should return 404 when user does not exist")
        void shouldReturn404_whenUserNotFound() {
            var expectedError = readResourceFile("user/patch/activate-user-not-found-404.json");

            String response = api()
                    .patch("/{id}/activate", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTENT_ID))
                    .asString();

            assertJson(response, expectedError, "timestamp");
        }

        @Test
        @DisplayName("Should return 400 when user is already active")
        void shouldReturn400_whenUserAlreadyActive() {
            var expectedError = readResourceFile("user/patch/user-already-active-400.json");

            String response = api()
                    .patch("/{id}/activate", HttpStatus.BAD_REQUEST, Map.of("id", ACTIVE_USER_ID))
                    .asString();

            assertJson(response, expectedError, "timestamp");
        }
    }

    @Nested
    @DisplayName("PATCH v1/user/{id}/deactivate")
    class DeactivateUserEndpoint {
        @Test
        @DisplayName("Should return 204 and deactivate user when user exists and is active")
        void shouldDeactivateUser_andReturn204() {
            api().patch("/{id}/deactivate", HttpStatus.NO_CONTENT, Map.of("id", ACTIVE_USER_ID)).asString();

            var user = userRepository.findById(ACTIVE_USER_ID).orElseThrow();
            assertThat(user.getActive()).isFalse();
        }

        @Test
        @DisplayName("Should return 404 when user does not exist")
        void shouldReturn404_whenUserNotFound() {
            var expectedError = readResourceFile("user/patch/deactivate-user-not-found-404.json");

            String response = api()
                    .patch("/{id}/deactivate", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTENT_ID))
                    .asString();

            assertJson(response, expectedError, "timestamp");
        }

        @Test
        @DisplayName("Should return 400 when user is already inactive")
        void shouldReturn400_whenUserAlreadyInactive() {
            var expectedError = readResourceFile("user/patch/user-already-inactive-400.json");

            String response = api()
                    .patch("/{id}/deactivate", HttpStatus.BAD_REQUEST, Map.of("id", INACTIVE_USER_ID))
                    .asString();

            assertJson(response, expectedError, "timestamp");
        }

    }

    record ApiClient(int port) {
        private static final String BASE_URI = "http://localhost:%d/v1/user";

        private RequestSpecification baseRequest() {
            return given()
                    .baseUri(BASE_URI.formatted(port))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON);
        }

        ExtractableResponse<Response> patch(String path, HttpStatus status, Map<String, ?> pathParams) {
            return baseRequest()
                    .pathParams(pathParams)
                    .patch(path)
                    .then()
                    .statusCode(status.value())
                    .extract();
        }

        ExtractableResponse<Response> patch(String path, HttpStatus expectedStatus) {
            return patch(path, expectedStatus, Map.of());
        }

    }

    private void assertJson(String actual, String expected, String... ignoredPaths) {
        var assertion = JsonAssertions.assertThatJson(actual);

        if (ignoredPaths.length > 0) {
            assertion.whenIgnoringPaths(ignoredPaths);
        }

        assertion
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expected);
    }
}
