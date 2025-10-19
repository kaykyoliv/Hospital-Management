package com.kayky.domain.operation;

import com.kayky.domain.operation.request.OperationBaseRequest;
import com.kayky.domain.operation.response.OperationBaseResponse;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.report.response.ReportBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationBaseResponse toOperationBaseResponse(Operation operation);

    @Mapping(target = "doctorName", source = "doctorFirstName")
    @Mapping(target = "patientName", source = "patientFirstName")
    OperationDetailsResponse toOperationDetailsResponse(OperationProjection operation);

    Operation toEntity(OperationBaseRequest postRequest);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateOperationFromRequest(OperationBaseRequest request, @MappingTarget Operation operation);

    @Named("toOperationInfo")
    default ReportBaseResponse.OperationInfo toOperationInfo(Operation operation) {
        if (operation == null) return null;
        return new ReportBaseResponse.OperationInfo(
                operation.getId(),
                operation.getDescription(),
                operation.getScheduledAt(),
                operation.getStatus().name()
        );
    }

}
