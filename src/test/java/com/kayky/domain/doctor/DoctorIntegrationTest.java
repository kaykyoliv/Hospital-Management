package com.kayky.domain.doctor;

import com.kayky.config.BaseIntegrationTest;
import com.kayky.domain.doctor.response.DoctorBaseResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static com.kayky.commons.FileUtils.readResourceFile;
import static com.kayky.commons.TestConstants.EXISTING_ID;
import static com.kayky.commons.TestConstants.NON_EXISTING_ID;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/doctor/sql/cleanup-doctor-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/doctor/sql/doctor-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Doctor Controller - Integration Tests")
public class DoctorIntegrationTest extends BaseIntegrationTest {

    private static final String GET = "doctor/get/";
    private static final String POST = "doctor/post/";

    private ApiClient api() {
        return new ApiClient(port);
    }

    @Nested
    @DisplayName("GET /v1/doctor")
    class GetEndpoints {
        @Test
        @DisplayName("GET /v1/doctor/{id} - Should return 200 with doctor data when doctor exists")
        void shouldReturnDoctor_whenIdExists() {
            var expectedResponse = readResourceFile(GET + "doctor-by-id-200.json");

            var response = api().get("/{id}", HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            assertJson(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/doctor/{id} - Should return 404 when doctor does not exist")
        void shouldReturn404_whenIdDoesNotExist() {
            var expectedResponse = readResourceFile(GET + "doctor-not-found-404.json");

            var response = api().get("/{id}", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJson(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/doctor - Should return 200 with paged doctor data when doctors exist")
        void shouldReturnPagedDoctors_whenDoctorsExist() {
            var expectedResponse = readResourceFile(GET + "all-paged-doctors-200.json");

            var response = api().get("", HttpStatus.OK).asString();

            assertJson(response, expectedResponse, "content[*].id");

            JsonAssertions.assertThatJson(response)
                    .node("content").isArray().hasSize(3);

            JsonAssertions.assertThatJson(response)
                    .and(json -> {
                        json.node("content[0].id").asNumber().isPositive();
                        json.node("content[1].id").asNumber().isPositive();
                        json.node("content[2].id").asNumber().isPositive();
                    });
        }
    }

    @Nested
    @DisplayName("POST /v1/doctor")
    class PostEndpoints {
        @Test
        @DisplayName("POST /v1/doctor - Should return 201 with doctor data when request is valid")
        @Sql(value = "/doctor/sql/cleanup-doctor-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void shouldReturn201_WhenRequestIsValida_onPost(){
            var request = readResourceFile(POST + "request-create-doctor-201.json");
            var expectedResponse = readResourceFile(POST + "response-created-doctor-201.json");

            var response = api().post("", request, HttpStatus.CREATED);

            assertThat(response.header(HttpHeaders.LOCATION))
                    .matches(".*/v1/doctor/\\d+$");

            var doctor = response.as(DoctorBaseResponse.class);
            var json = response.asString();

            assertThat(doctor.getId()).isPositive();

            assertJson(json, expectedResponse, "id");
        }

        @Test
        @DisplayName("POST /v1/doctor - Should return 400 when email already exists")
        void shouldReturn400_whenEmailAlreadyExists_onPost(){
            var request = readResourceFile(POST + "request-create-doctor-201.json");
            var expectedResponse = readResourceFile(POST + "response-email-already-exists-400.json");

            var response = api().post("", request, HttpStatus.BAD_REQUEST).asString();

            assertJson(response, expectedResponse, "timestamp");
        }

        @DisplayName("POST /v1/doctor - Should return 422 when request is invalid")
        @Test
        void shouldReturn422_whenRequestIsInvalid_onPost() {
            var request = readResourceFile(POST + "request-create-doctor-invalid-422.json");
            var expectedResponse = readResourceFile(POST + "validation-error-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJson(response, expectedResponse, "timestamp");
        }
    }

    record ApiClient(int port) {
        private static final String BASE_URI = "http://localhost:%d/v1/doctor";

        private RequestSpecification baseRequest() {
            return given()
                    .baseUri(BASE_URI.formatted(port))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON);
        }

        ExtractableResponse<Response> get(String path, HttpStatus status, Map<String, ?> pathParams) {
            return baseRequest()
                    .pathParams(pathParams)
                    .get(path)
                    .then()
                    .statusCode(status.value())
                    .extract();
        }

        ExtractableResponse<Response> get(String path, HttpStatus status) {
            return get(path, status, Map.of());
        }

        ExtractableResponse<Response> post(String path, String body, HttpStatus status){
            return baseRequest()
                    .body(body)
                    .post(path)
                    .then()
                    .statusCode(status.value())
                    .extract();
        }

        ExtractableResponse<Response> put(String path, String body, HttpStatus status, Map<String, ?> pathParams) {
            return baseRequest()
                    .pathParams(pathParams)
                    .body(body)
                    .put(path)
                    .then()
                    .statusCode(status.value())
                    .extract();
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
