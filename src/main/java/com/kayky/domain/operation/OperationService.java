package com.kayky.domain.operation;

import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.operation.request.OperationPostRequest;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.operation.response.OperationGetResponse;
import com.kayky.domain.operation.response.OperationPostResponse;
import com.kayky.domain.user.UserValidator;
import com.kayky.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationService {

    private final OperationRepository repository;
    private final UserValidator userValidator;

    private final OperationMapper mapper;

    @Transactional(readOnly = true)
    public OperationGetResponse findById(Long id){
        return repository.findById(id)
                .map(mapper::toOperationGetResponse)
                .orElseThrow(() -> {
                    log.warn("Operation not found with id {}", id);

                    return new ResourceNotFoundException("Operation not found");
                });
    }

    @Transactional(readOnly = true)
    public PageResponse<OperationDetailsResponse> findAll(Pageable pageable){
        var page = repository.findAllProjected(pageable);
        return PageUtils.mapPage(page, mapper::toOperationDetailsResponse);
    }


    public OperationPostResponse save(OperationPostRequest request){
        userValidator.assertIfUserExist(request.getPatient().getId(), "Patient");
        userValidator.assertIfUserExist(request.getDoctor().getId(), "Doctor");

        var operationToSave = mapper.toEntity(request);
        var savedOperation = repository.save(operationToSave);

        return mapper.toOperationPostResponse(savedOperation);
    }
}
