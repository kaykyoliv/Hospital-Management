package com.kayky.domain.patient;


import com.kayky.config.BaseIntegrationTest;
import com.kayky.domain.patient.response.PatientBaseResponse;
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
import static com.kayky.commons.JsonTestUtils.assertJsonEquals;
import static com.kayky.commons.TestConstants.EXISTING_ID;
import static com.kayky.commons.TestConstants.NON_EXISTING_ID;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/patient/sql/cleanup-patient-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/patient/sql/patient-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Patient Controller - Integration Tests")
public class PatientIntegrationTest extends BaseIntegrationTest {

    private static final String GET = "patient/get/";
    private static final String POST = "patient/post/";
    private static final String PUT = "patient/put/";

    private ApiClient api() {
        return new ApiClient(port);
    }

    @Nested
    @DisplayName("GET /v1/patient")
    class GetEndpoints {
        @Test
        @DisplayName("GET /v1/patient/{id} - Should return 200 with patient data when patient exists")
        void shouldReturnPatient_whenIdExists() {
            var expectedResponse = readResourceFile(GET + "patient-by-id-200.json");

            var response = api().get("/{id}", HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/patient/{id} - Should return 404 when patient does not exist")
        void shouldReturn404_whenIdDoesNotExist() {
            var expectedResponse = readResourceFile(GET + "patient-not-found-404.json");

            var response = api().get("/{id}", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("GET /v1/patient - Should return 200 with paged patient data when patients exist")
        void shouldReturnPagedPatients_whenPatientsExist() {
            var expectedResponse = readResourceFile(GET + "all-paged-patients-200.json");

            var response = api().get("", HttpStatus.OK).asString();

            JsonAssertions.assertThatJson(response)
                    .whenIgnoringPaths("content[*].id")
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo(expectedResponse);

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
    @DisplayName("POST /v1/patient")
    class PostEndpoints {
        @Test
        @DisplayName("POST /v1/patient - Should return 201 with patient data when request is valid")
        @Sql(value = "/patient/sql/cleanup-patient-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void shouldReturn201_whenRequestIsValid_onPost() {
            var request = readResourceFile(POST + "request-create-patient-201.json");
            var expectedResponse = readResourceFile(POST + "response-created-patient-201.json");

            var response = api().post("", request, HttpStatus.CREATED);

            assertThat(response.header(HttpHeaders.LOCATION))
                    .matches(".*/v1/patient/\\d+$");

            var patient = response.as(PatientBaseResponse.class);
            var json = response.asString();

            assertThat(patient.getId()).isPositive();

            assertJsonEquals(json, expectedResponse, "id");
        }

        @Test
        @DisplayName("POST /v1/patient - Should return 400 when email already exists")
        void shouldReturn400_whenEmailAlreadyExists_onPost() {
            var request = readResourceFile(POST + "request-email-already-exists.json");
            var expectedResponse = readResourceFile(POST + "response-email-already-exists-400.json");

            var response = api().post("", request, HttpStatus.BAD_REQUEST).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }


        @DisplayName("POST /v1/patient - Should return 422 when request is invalid")
        @Test
        void shouldReturn422_whenRequestIsInvalid_onPost() {
            var request = readResourceFile(POST + "request-create-patient-invalid-422.json");
            var expectedResponse = readResourceFile(POST + "validation-error-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }

    @Nested
    @DisplayName("PUT /v1/patient/{id}")
    class PutEndpoints {
        @Test
        @DisplayName("PUT /v1/patient - Should return 200 with updated patient data when request is valid")
        void shouldReturn200_whenRequestIsValid_onPut() {
            var request = readResourceFile(PUT + "request-update-patient.json");
            var expectedResponse = readResourceFile(PUT + "response-updated-patient-200.json");

            var response = api().put("/{id}", request, HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            JsonAssertions.assertThatJson(response)
                    .whenIgnoringPaths("id")
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo(expectedResponse)
                    .node("id").isEqualTo(EXISTING_ID);
        }


        @Test
        @DisplayName("PUT /v1/patient - Should return 404 when patient does not exist")
        void shouldReturn404_whenPatientDoesNotExist_onPut() {
            var request = readResourceFile(PUT + "request-update-patient.json");
            var expectedResponse = readResourceFile(PUT + "response-patient-not-found-404.json");

            var response = api().put("/{id}", request, HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("PUT /v1/patient - Should return 400 when email already exists")
        void shouldReturn400_whenEmailAlreadyExists_onPut() {
            var request = readResourceFile(PUT + "request-email-already-exists.json");
            var expectedResponse = readResourceFile(PUT + "response-email-already-exists-400.json");

            var response = api().put("/{id}", request, HttpStatus.BAD_REQUEST, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id", "timestamp");
        }


        @Test
        @DisplayName("PUT /v1/patient - Should return 422 when request is invalid")
        void shouldReturn422_whenRequestIsInvalid_onPut() {
            var request = readResourceFile(PUT + "request-update-patient-invalid-422.json");
            var expectedResponse = readResourceFile(PUT + "validation-error-422.json");

            var response = api().put("/{id}", request, HttpStatus.UNPROCESSABLE_ENTITY, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }

    record ApiClient(int port) {
        private static final String BASE_URI = "http://localhost:%d/v1/patient";

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

        ExtractableResponse<Response> post(String path, String body, HttpStatus status) {
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
}
