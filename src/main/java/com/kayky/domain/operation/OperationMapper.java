package com.kayky.domain.operation;

import com.kayky.domain.operation.request.OperationPostRequest;
import com.kayky.domain.operation.request.OperationPutRequest;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.operation.response.OperationGetResponse;
import com.kayky.domain.operation.response.OperationPostResponse;
import com.kayky.domain.operation.response.OperationPutResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationGetResponse toOperationGetResponse(Operation operation);

    @Mapping(target = "doctorName", source = "doctorFirstName")
    @Mapping(target = "patientName", source = "patientFirstName")
    OperationDetailsResponse toOperationDetailsResponse(OperationProjection operation);

    Operation toEntity(OperationPostRequest postRequest);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateOperationFromRequest(OperationPutRequest request, @MappingTarget Operation operation);

    OperationPostResponse toOperationPostResponse(Operation operation);

    OperationPutResponse toOperationPutResponse(Operation operation);

}
