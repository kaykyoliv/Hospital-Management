package com.kayky.domain.cashier;

import com.kayky.config.BaseIntegrationTest;
import com.kayky.domain.cashier.response.CashierBaseResponse;
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

@DisplayName("Cashier Controller - Integration Tests")
@Sql(value = "/cashier/sql/cleanup-cashier-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/cashier/sql/cashier-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CashierIntegrationTest extends BaseIntegrationTest {

    private static final String GET = "cashier/integration/get/";
    private static final String POST = "cashier/integration/post/";
    private static final String PUT = "cashier/integration/put/";

    private ApiClient api() {
        return new ApiClient(port);
    }


    @Nested
    @DisplayName("GET /v1/cashier/{id}")
    class GetEndPoints {
        @Test
        @DisplayName("GET /v1/cashier/{id} - Should return 200 with cashier data when cashier exists")
        void shouldReturnCashier_whenIdExists() {
            var expectedResponse = readResourceFile(GET + "cashier-by-id-200.json");

            var response = api().get("/{id}", HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/cashier/{id} - Should return 404 when cashier does not exists")
        void shouldReturn404_whenIdDoesNotExists() {
            var expectedResponse = readResourceFile(GET + "cashier-not-found-404.json");

            var response = api().get("/{id}", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/cashier - Should return 200 with paged cashier data when cashiers exists")
        void shouldReturnPagedCashiers_whenCashiersExists() {
            var expectedResponse = readResourceFile(GET + "all-paged-cashiers-200.json");

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
    @DisplayName("POST /v1/cashier")
    class PostEndPoints {

        @Test
        @DisplayName("POST /v1/cashier - Should return 201 with cashier data when request is valid")
        void shouldReturn201_whenValidRequest(){
            var request = readResourceFile(POST + "request/request-create-report-201.json");
            var expectedResponse = readResourceFile(POST + "response/response-created-report-201.json");

            var response = api().post("", request, HttpStatus.CREATED);
            assertThat(response.header(HttpHeaders.LOCATION)).matches(".*/v1/cashier/\\d+$");

            var cashier = response.as(CashierBaseResponse.class);
            var json = response.asString();

            assertThat(cashier.id()).isPositive();

            assertJsonEquals(json, expectedResponse, "id");
        }

        @Test
        @DisplayName("POST /v1/cashier - Should return 400 when email already exists")
        void shouldReturn400_whenEmailAlreadyExists(){
            var request = readResourceFile(POST + "request/request-email-already-exists-400.json");
            var expectedResponse = readResourceFile(POST + "response/response-email-already-exists-400.json");

            var response = api().post("", request, HttpStatus.BAD_REQUEST).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }

        @Test
        @DisplayName("POST /v1/cashier - Should return 422 when request is invalid")
        void shouldReturn422_whenRequestIsInvalid(){
            var request = readResourceFile(POST + "request/request-create-cashier-invalid-422.json");
            var expectedResponse = readResourceFile(POST + "response/response-validation-error-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }

    @Nested
    @DisplayName("PUT /v1/cashier/{id}")
    class PutEndPoints {

        @Test
        @DisplayName("PUT /v1/cashier/{id} - Should return 200 updated cashier when request is valid")
        void shouldReturn200_whenValidRequest(){
            var request = readResourceFile(PUT + "request/request-update-cashier-200.json");
            var expectedResponse = readResourceFile(PUT + "response/response-updated-cashier-200.json");

            var response = api().put("/{id}", request, HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            JsonAssertions.assertThatJson(response)
                    .whenIgnoringPaths("id")
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo(expectedResponse)
                    .node("id").isEqualTo(EXISTING_ID);
        }

        @Test
        @DisplayName("PUT /v1/cashier/{id} - Should return 400 when email already exists")
        void shouldReturn400_whenEmailAlreadyExists(){
            var request = readResourceFile(PUT + "request/request-email-already-exists-400.json");
            var expectedResponse = readResourceFile(PUT + "response/response-email-already-exists-400.json");

            var response = api().put("/{id}", request, HttpStatus.BAD_REQUEST, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id", "timestamp");
        }

        @Test
        @DisplayName("PUT /v1/cashier/{id} - Should return 404 when cashier does not exist")
        void shouldReturn404_whenCashierDoesNotExist(){
            var request = readResourceFile(PUT + "request/request-cashier-not-found-404.json");
            var expectedResponse = readResourceFile(PUT + "response/response-cashier-not-found-404.json");

            var response = api().put("/{id}", request, HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("PUT /v1/cashier/{id} - Should return 422 when request is invalid")
        void shouldReturn422_whenRequestIsInvalid() {
            var request = readResourceFile(PUT + "request/request-update-cashier-invalid-422.json");
            var expectedResponse = readResourceFile(PUT + "response/response-validation-error-422.json");

            var response = api().put("/{id}", request, HttpStatus.UNPROCESSABLE_ENTITY, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }


    record ApiClient(int port) {
        private static final String BASE_URI = "http://localhost:%d/v1/cashier";

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

        ExtractableResponse<Response> post(String path, String body, HttpStatus status) {
            return baseRequest()
                    .body(body)
                    .post(path)
                    .then()
                    .statusCode(status.value())
                    .extract();
        }


        ExtractableResponse<Response> get(String path, HttpStatus status) {
            return get(path, status, Map.of());
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
