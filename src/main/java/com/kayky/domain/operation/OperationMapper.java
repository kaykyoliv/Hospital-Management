package com.kayky.domain.operation;

import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.operation.response.OperationGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationGetResponse toOperationGetResponse(Operation operation);

    @Mapping(target = "doctorName", source = "doctorFirstName")
    @Mapping(target = "patientName", source = "patientFirstName")
    OperationDetailsResponse toOperationDetailsResponse(OperationProjection operation);


}
