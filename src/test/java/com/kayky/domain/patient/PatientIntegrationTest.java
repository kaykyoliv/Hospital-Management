package com.kayky.domain.patient;


import com.kayky.commons.FileUtils;
import com.kayky.config.BaseIntegrationTest;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static com.kayky.commons.TestConstants.EXISTING_ID;
import static io.restassured.RestAssured.given;

public class PatientIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URI = "/v1/patient";

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 200 with patient data when patient exists")
    @Sql(value = "/patient/sql/patient-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldReturnPatient_whenIdExists() {
        Map<String, Object> expectedResponse = FileUtils.readResourceAsJson("patient/get/patient-by-id-200.json");

        var response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .pathParam("id", EXISTING_ID)
                .get(BASE_URI + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);
    }
}
