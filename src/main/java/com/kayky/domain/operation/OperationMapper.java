package com.kayky.domain.operation;

import com.kayky.domain.operation.request.OperationBaseRequest;
import com.kayky.domain.operation.response.OperationBaseResponse;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

}
