package com.kayky.domain.report;

import com.kayky.config.BaseIntegrationTest;
import com.kayky.domain.report.response.ReportBaseResponse;
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

@DisplayName("Report Controller - Integration Tests")
@Sql(value = "/report/sql/cleanup-report-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/report/sql/report-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReportIntegrationTest extends BaseIntegrationTest {

    private static final String GET = "report/integration/get/";
    private static final String POST = "report/integration/post/";

    private ApiClient api() {
        return new ApiClient(port);
    }

    @Nested
    @DisplayName("GET /v1/report/{id}")
    class GetEndPoints {
        @Test
        @DisplayName("GET /v1/report/{id} - Should return 200 with report data when report exists")
        void shouldReturnReport_whenIdExists() {
            var expectedResponse = readResourceFile(GET + "report-by-id-200.json");

            var response = api().get("/{id}", HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/report/{id} - Should return 404 when report does not exists")
        void shouldReturn404_whenIdDoesNotExists() {
            var expectedResponse = readResourceFile(GET + "report-not-found-404.json");

            var response = api().get("/{id}", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/report = Should return 200 with paged report data when reports exists")
        void shouldReturnPagedReports_whenReportsExists(){
            var expectedResponse = readResourceFile(GET + "all-paged-reports-200.json");

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
    @DisplayName("POST /v1/report")
    class PostEndPoints{
        @Test
        @DisplayName("POST /v1/report - Should return 201 with report data when request is valid")
        @Sql(value = "/report/sql/cleanup-report-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/report/sql/report-post-base-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void shouldReturn201_whenValidRequest(){
            var request = readResourceFile(POST + "request/request-create-report-201.json");
            var expectedResponse = readResourceFile(POST + "response/response-created-report-201.json");

            var response = api().post("", request, HttpStatus.CREATED);
            assertThat(response.header(HttpHeaders.LOCATION)).matches(".*/v1/report/\\d+$");

            var report = response.as(ReportBaseResponse.class);
            var json = response.asString();

            assertThat(report.id()).isPositive();

            assertJsonEquals(json, expectedResponse, "id", "createdAt", "updatedAt");
        }

        @Test
        @DisplayName("POST /v1/report - Should return 400 when operation does not belong to the given patient")
        @Sql(value = "/report/sql/cleanup-report-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/report/sql/report-post-base-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void shouldReturn400_whenOperationDoesNotBelongToPatient(){
            var request = readResourceFile(POST + "request/request-create-report-400-operation-patient-mismatch.json");
            var expectedResponse = readResourceFile(POST + "response/response-create-report-400-operation-patient-mismatch.json");

            var response = api().post("", request, HttpStatus.BAD_REQUEST).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/report - Should return 400 when operation does not belong to the given doctor")
        @Sql(value = "/report/sql/cleanup-report-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/report/sql/report-post-base-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void shouldReturn400_whenOperationDoesNotBelongToDoctor(){
            var request = readResourceFile(POST + "request/request-create-report-400-operation-doctor-mismatch.json");
            var expectedResponse = readResourceFile(POST + "response/response-create-report-400-operation-doctor-mismatch.json");

            var response = api().post("", request, HttpStatus.BAD_REQUEST).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/report - Should return 409 when report already exists for the operation")
        void shouldReturn409_whenReportAlreadyExistsForOperation(){
            var request = readResourceFile(POST + "request/request-create-report-409-conflict.json");
            var expectedResponse = readResourceFile(POST + "response/response-create-report-409-conflict.json");

            var response = api().post("", request, HttpStatus.CONFLICT).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }

    record ApiClient(int port) {
        private static final String BASE_URI = "http://localhost:%d/v1/report";

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

    }
}
