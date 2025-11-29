package com.kayky.domain.patient;


import com.jayway.jsonpath.JsonPath;
import com.kayky.config.BaseIntegrationTest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.kayky.commons.FileUtils.readResourceFile;
import static com.kayky.commons.TestConstants.EXISTING_ID;
import static com.kayky.commons.TestConstants.NON_EXISTING_ID;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;

public class PatientIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URI = "/v1/patient";

    private static final String POST = "patient/post/";
    private static final String GET = "patient/get/";

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 200 with patient data when patient exists")
    @Sql(value = "/patient/sql/cleanup-patient-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/patient/sql/patient-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldReturnPatient_whenIdExists() {
        var expectedResponse = readResourceFile(GET + "patient-by-id-200.json");

        var response = given()
                .contentType(JSON)
                .accept(JSON)
                .when()
                .pathParam("id", EXISTING_ID)
                .get(BASE_URI + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 404 when patient does not exist")
    @Sql(value = "/patient/sql/cleanup-patient-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldReturn404_whenIdDoesNotExist() {
        var expectedResponse = readResourceFile(GET + "patient-not-found-404.json");

        var response = given()
                .contentType(JSON)
                .accept(JSON)
                .when()
                .pathParam("id", NON_EXISTING_ID)
                .get(BASE_URI + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().ifValidationFails()
                .extract()
                .asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET /v1/patient - Should return 200 with paged patient data when patients exist")
    @Sql(value = "/patient/sql/cleanup-patient-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/patient/sql/patient-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldReturnPagedPatients_whenPatientsExist() {
        var expectedResponse = readResourceFile(GET + "all-paged-patients-200.json");

        var response = given()
                .contentType(JSON).accept(JSON)
                .when()
                .get(BASE_URI)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails()
                .extract().response().body().asString();

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
    @DisplayName("POST /v1/patient - Should return 201 with patient data when request is valid")
    @Sql(value = "/patient/sql/cleanup-patient-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldReturn201_whenRequestIsValid_onPost() {
        var request = readResourceFile(POST + "request-create-patient-201.json");
        var expectedResponse = readResourceFile(POST + "response-created-patient-201.json");

        var response = given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(BASE_URI)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/v1/patient/"))
                .header(HttpHeaders.LOCATION, matchesPattern(".*/v1/patient/\\d+$"))
                .log().ifValidationFails()
                .extract().response();

        var patient = response.as(PatientBaseResponse.class);
        var json = response.asString();

        assertThat(patient.getId()).isPositive();

        JsonAssertions.assertThatJson(json)
                .whenIgnoringPaths("id")
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST /v1/patient - Should return 400 when email already exists")
    @Sql(value = "/patient/sql/cleanup-patient-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/patient/sql/patient-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldReturn400_whenEmailAlreadyExists_onCreate() {
        var request = readResourceFile(POST + "request-create-patient-201.json");
        var expectedResponse = readResourceFile(POST + "response-email-already-exists-400.json");

        var response = given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(BASE_URI)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().ifValidationFails()
                .extract()
                .asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expectedResponse);
    }


    @DisplayName("POST /v1/patient - Should return 422 when request is invalid")
    @Test
    void shouldReturn422_whenRequestIsInvalid_onPost() {
        var request = readResourceFile(POST + "request-create-patient-invalid-422.json");
        var expectedResponse = readResourceFile(POST + "validation-error-422.json");

        var response = given()
                .contentType(JSON)
                .body(request)
                .when()
                .post(BASE_URI)
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .log().ifValidationFails()
                .extract()
                .asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expectedResponse);
    }

}
