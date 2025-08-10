package com.kayky.domain.operation.response;

import com.kayky.domain.operation.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationDetailsResponse {

    private Long id;
    private String description;
    private LocalDateTime scheduledAt;

    private String doctorName;
    private String patientName;

    private OperationStatus status;
}
