package com.kayky.domain.payment;

import com.kayky.config.BaseIntegrationTest;
import com.kayky.domain.payment.response.PaymentBaseResponse;
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

@Sql(value = "/payment/sql/cleanup-payment-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/payment/sql/payment-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/payment/sql/cleanup-payment-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class PaymentIntegrationTest extends BaseIntegrationTest {

    private static final String GET = "payment/integration/get/";
    private static final String POST = "payment/integration/post/";

    private ApiClient api() {
        return new ApiClient(port);
    }

    @Nested
    @DisplayName("GET /v1/payment/{id}")
    class GetEndPoints {
        private static final int PATIENT_ID = 2;

        @Test
        @DisplayName("GET /v1/payment/{id} - Should return 200 when request is valid")
        void shouldReturnPayment_whenIdExists() {
            var expectedResponse = readResourceFile(GET + "payment-by-id-200.json");

            var response = api().get("/{id}", HttpStatus.OK, Map.of("id", EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/payment/{id} - Should return 404 when payment does not exists")
        void shouldReturn404_whenIdDoesNotExist() {
            var expectedResponse = readResourceFile(GET + "payment-not-found-404.json");

            var response = api().get("/{id}", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/payment - Should return 200 with paged payments when payments exist")
        void shouldReturnPagedPayments_whenPaymentsExists() {
            var expectedResponse = readResourceFile(GET + "all-paged-payments-200.json");

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

        @Test
        @DisplayName("GET /v1/payment/patients/{patientId}/payments - Should return 200 with payment list when patient exists")
        void shouldReturnPayments_whenPatientExists() {
            var expectedResponse = readResourceFile(GET + "payments-by-patient-200.json");

            var response = api().get("patients/{id}/payments", HttpStatus.OK, Map.of("id", PATIENT_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("GET /v1/payment/patients/{patientId}/payments - Should return 404 when patient does not exist")
        void shouldReturn404_whenPatientDoesNotExist() {
            var expectedResponse = readResourceFile(GET + "payments-by-patient-404.json");

            var response = api().get("patients/{id}/payments", HttpStatus.NOT_FOUND, Map.of("id", NON_EXISTING_ID)).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }
    }

    @Nested
    @DisplayName("POST /v1/payment")
    class PostEndPoints {

        @Test
        @DisplayName("POST /v1/payment - Should return 201 with payment data when request is valid")
        @Sql(value = "/payment/sql/cleanup-payment-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/payment/sql/payment-post-base-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void shouldReturn201_whenValidRequest() {
            var request = readResourceFile(POST + "request/request-create-payment-201.json");
            var expectedResponse = readResourceFile(POST + "response/response-created-payment-201.json");

            var response = api().post("", request, HttpStatus.CREATED);
            assertThat(response.header(HttpHeaders.LOCATION)).matches(".*/v1/payment/\\d+$");

            var payment = response.as(PaymentBaseResponse.class);
            var json = response.asString();

            assertThat(payment.id()).isPositive();

            assertJsonEquals(json, expectedResponse, "id", "paymentDate");
        }

        @Test
        @DisplayName("POST /v1/payment - Should return 404 when cashier does not exist")
        void shouldReturn404_whenCashierDoesNotExist() {
            var request = readResourceFile(POST + "request/request-cashier-not-found-404.json");
            var expectedResponse = readResourceFile(POST + "response/response-cashier-not-found-404.json");

            var response = api().post("", request, HttpStatus.NOT_FOUND).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("POST /v1/payment - Should return 404 when patient does not exist")
        void shouldReturn404_whenPatientDoesNotExist() {
            var request = readResourceFile(POST + "request/request-patient-not-found-404.json");
            var expectedResponse = readResourceFile(POST + "response/response-patient-not-found-404.json");

            var response = api().post("", request, HttpStatus.NOT_FOUND).asString();

            assertJsonEquals(response, expectedResponse, "id");
        }

        @Test
        @DisplayName("POST /v1/payment - Should return 422 when request is invalid")
        void shouldReturn422_whenRequestIsInvalid() {
            var request = readResourceFile(POST + "request/request-create-payment-invalid-422.json");
            var expectedResponse = readResourceFile(POST + "response/response-validation-error-422.json");

            var response = api().post("", request, HttpStatus.UNPROCESSABLE_ENTITY).asString();

            assertJsonEquals(response, expectedResponse, "timestamp");
        }
    }


    record ApiClient(int port) {
        private static final String BASE_URI = "http://localhost:%d/v1/payment";

        private RequestSpecification baseRequest() {
            return given()
                    .baseUri(BASE_URI.formatted(port))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON);
        }

        private ExtractableResponse<Response> get(String path, HttpStatus status, Map<String, ?> pathParams) {
            return baseRequest()
                    .pathParams(pathParams)
                    .get(path)
                    .then()
                    .statusCode(status.value())
                    .extract();
        }

        private ExtractableResponse<Response> get(String path, HttpStatus status) {
            return get(path, status, Map.of());
        }

        private ExtractableResponse<Response> post(String path, String body, HttpStatus status) {
            return baseRequest()
                    .body(body)
                    .post(path)
                    .then()
                    .statusCode(status.value())
                    .extract();
        }
    }
}
