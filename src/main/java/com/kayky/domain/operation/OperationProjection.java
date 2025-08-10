package com.kayky.domain.operation;

import java.time.LocalDateTime;

public interface OperationProjection {
    Long getId();
    String getDescription();
    LocalDateTime getScheduledAt();
    String getDoctorFirstName();
    String getPatientFirstName();
    OperationStatus getStatus();
}