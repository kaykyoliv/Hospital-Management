package com.kayky.domain.operation;

import com.kayky.core.pagination.PageMapper;
import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.operation.response.OperationGetResponse;
import com.kayky.domain.operation.response.OperationPageResponse;
import com.kayky.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationService {

    private final OperationRepository repository;
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
}
