package com.kayky.domain.operation.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OperationPageResponse {

    private List<OperationDetailsResponse> operations;

    private int totalPages;
    private long totalElements;
    private int currentPage;
}
