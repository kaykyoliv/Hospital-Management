package com.kayky.domain.operation;

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
    public OperationPageResponse findAll(Pageable pageable){
        var page = repository.findAllProjected(pageable);

        return OperationPageResponse.builder()
                .operations(page.getContent()
                        .stream()
                        .map(mapper::toOperationDetailsResponse)
                        .toList())
                .currentPage(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
