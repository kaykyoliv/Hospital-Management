package com.kayky.domain.operation.response;

import com.kayky.domain.operation.OperationStatus;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationDetailsResponse {

    private Long id;
    private String description;
    private LocalDateTime scheduledAt;

    private String doctorName;
    private String patientName;

    private OperationStatus status;
}
