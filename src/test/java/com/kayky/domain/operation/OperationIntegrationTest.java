package com.kayky.domain.operation;

import com.kayky.config.BaseIntegrationTest;
import com.kayky.domain.operation.response.OperationBaseResponse;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Operation Controller - Integration Tests")
@Sql(value = "/operation/sql/cleanup-operation-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/operation/sql/operation-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class OperationIntegrationTest extends BaseIntegrationTest {

    private static final String GET = "operation/integration/get/";
    private static final String POST = "operation/integration/post/";
    private static final String PUT = "operation/integration/put/";

    private ApiClient api() {
        return new ApiClient(port);
    }

    @Nested
    @DisplayName("GET /v1/operation")
    class GetEndPoints {
        @Test
        @DisplayName("GET /v1/operation/{id} - Should return 200 with operation data when operation exists")
        void shouldReturnOperation_whenIdExists() {
            var expectedResponse = readResourceFile(GET + "operation-by-id-200.json");

            var response = api().get("/{id}", HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/operation/{id} - Should return 404 when operation does not exists")
        void shouldReturn404_whenIdDoesNotExist() {
            var expectedResponse = readResourceFile(GET + "operation-not-found-404.json");

            var response = api().get("/{id}", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/operation - Should return 200 with paged operation data when operations exist")
        void shouldReturnPagedOperations_whenOperationsExist() {
            var expectedResponse = readResourceFile(GET + "all-paged-operations-200.json");

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
    @DisplayName("POST /v1/operation")
    class PostEndPoints {
        @Test
        @DisplayName("POST /v1/operation - Should return 201 with operation data when request is valid")
        @Sql(value = "/operation/sql/cleanup-operation-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/operation/sql/operation-post-base-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void shouldReturn201_whenValidRequest() {
            var request = readResourceFile(POST + "request/request-create-operation-201.json");
            var expectedResponse = readResourceFile(POST + "response/response-created-operation-201.json");

            var response = api().post("", request, HttpStatus.CREATED);
            assertThat(response.header(HttpHeaders.LOCATION)).matches(".*/v1/operation/\\d+$");

            var operation = response.as(OperationBaseResponse.class);
            var json = response.asString();

            assertThat(operation.getId()).isPositive();

            assertJsonEquals(json, expectedResponse, "id");
        }

        @Test
        @DisplayName("POST /v1/operation - Should return 400 when enum value is invalid")
        void shouldReturn400_whenEnumValueIsInvalid() {
            var request = readResourceFile(POST + "request/request-invalid-enum-400.json");
            var expectedResponse = readResourceFile(POST + "response/response-invalid-enum-400.json");

            var response = api().post("", request, HttpStatus.BAD_REQUEST).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/operation - Should return 400 when JSON is malformed")
        void shouldReturn400_whenJsonIsMalformed() {
            var request = readResourceFile(POST + "request/request-json-malformed-400.json");
            var expectedResponse = readResourceFile(POST + "response/response-json-malformed-400.json");

            var response = api().post("", request, HttpStatus.BAD_REQUEST).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/operation - Should return 404 when doctorId does not exist")
        void shouldReturn404_whenDoctorIdNotExists() {
            var request = readResourceFile(POST + "request/request-doctor-id-not-found-404.json");
            var expectedResponse = readResourceFile(POST + "response/response-doctor-id-not-found-404.json");

            var response = api().post("", request, HttpStatus.NOT_FOUND).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/operation - Should return 404 when patientId does not exist")
        void shouldReturn404_whenPatientIdNotExists() {
            var request = readResourceFile(POST + "request/request-patient-id-not-found-404.json");
            var expectedResponse = readResourceFile(POST + "response/response-patient-id-not-found-404.json");

            var response = api().post("", request, HttpStatus.NOT_FOUND).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/operation - Should return 422 when doctorId belongs to a non-doctor user")
        void shouldReturn422_whenDoctorIdIsNotADoctor() {
            var request = readResourceFile(POST + "request/request-doctor-id-not-a-doctor-422.json");
            var expectedResponse = readResourceFile(POST + "response/response-doctor-id-not-a-doctor-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/operation - Should return 422 when patientId belongs to a non-patient user")
        void shouldReturn422_whenPatientIdIsNotAPatient() {
            var request = readResourceFile(POST + "request/request-patient-id-not-a-patient-422.json");
            var expectedResponse = readResourceFile(POST + "response/response-patient-id-not-a-patient-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }


        @Test
        @DisplayName("POST /v1/operation - Should return 422 when required field is missing")
        void shouldReturn422_whenRequiredFieldIsMissing() {
            var request = readResourceFile(POST + "request/request-missing-description-422.json");
            var expectedResponse = readResourceFile(POST + "response/response-missing-field-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/operation - Should return 422 when scheduledAt is in the past")
        void shouldReturn422_whenScheduledAtIsInThePast() {
            var request = readResourceFile(POST + "request/request-scheduled-at-in-the-past-422.json");
            var expectedResponse = readResourceFile(POST + "response/response-scheduled-at-in-the-past-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }

    @Nested
    @DisplayName("PUT /v1/operation/{id}")
    class PutEndPoints {

        @Test
        @DisplayName("PUT /v1/operation/{id} - Should return 200 with updated operation data when request is valid")
        void shouldReturn200_whenValidRequest() {

            var request = readResourceFile(PUT + "request/request-update-operation.json");
            var expectedResponse = readResourceFile(PUT + "response/response-updated-operation.json");

            var response = api().put("/{id}", request, HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");

            JsonAssertions.assertThatJson(response)
                    .node("id").isEqualTo(EXISTING_ID);
        }

        @Test
        @DisplayName("PUT /v1/operation/{id} - Should return 400 when enum value is invalid")
        void shouldReturn400_whenEnumValueIsInvalid(){
            var request = readResourceFile(PUT + "request/request-invalid-enum-400.json");
            var expectedResponse = readResourceFile(PUT + "response/response-invalid-enum-400.json");

            var response = api().put("/{id}", request, HttpStatus.BAD_REQUEST, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("PUT /v1/operation/{id} - Should return 400 when JSON is malformed")
        void shouldReturn400_whenJsonIsMalformed(){
            var request = readResourceFile(PUT + "request/request-json-malformed-400.json");
            var expectedResponse = readResourceFile(PUT + "response/response-json-malformed-400.json");

            var response = api().put("/{id}", request, HttpStatus.BAD_REQUEST, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("PUT /v1/operation/{id} - Should return 404 when doctorId does not exist")
        void shouldReturn404_whenDoctorIdNotExists(){
            var request = readResourceFile(PUT + "request/request-doctor-id-not-found-404.json");
            var expectedResponse = readResourceFile(PUT + "response/response-doctor-id-not-found-404.json");

            var response = api().put("/{id}", request, HttpStatus.NOT_FOUND, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }

    record ApiClient(int port) {
        private static final String BASE_URI = "http://localhost:%d/v1/operation";

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
