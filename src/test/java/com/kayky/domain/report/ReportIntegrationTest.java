package com.kayky.domain.report;

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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static com.kayky.commons.FileUtils.readResourceFile;
import static com.kayky.commons.JsonTestUtils.assertJsonEquals;
import static com.kayky.commons.TestConstants.EXISTING_ID;
import static com.kayky.commons.TestConstants.NON_EXISTING_ID;
import static io.restassured.RestAssured.given;

@DisplayName("Report Controller - Integration Tests")
@Sql(value = "/report/sql/cleanup-report-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/report/sql/report-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReportIntegrationTest extends BaseIntegrationTest {

    private static final String GET = "report/integration/get/";

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

    }
}
