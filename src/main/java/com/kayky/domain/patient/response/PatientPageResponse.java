package com.kayky.domain.patient.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PatientPageResponse {

    @Schema(description = "List of patients for the current page")
    private List<PatientGetResponse> patients;

    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @Schema(description = "Total number of elements across all pages", example = "100")
    private long totalElements;

    @Schema(description = "Current page number (0-based index)", example = "0")
    private int currentPage;
}
