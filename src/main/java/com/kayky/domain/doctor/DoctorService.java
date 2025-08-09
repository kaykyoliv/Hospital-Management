package com.kayky.domain.doctor;

import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.doctor.response.DoctorPageResponse;
import com.kayky.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository repository;
    private final DoctorMapper mapper;

    @Transactional(readOnly = true)
    public DoctorGetResponse findById(Long id){
        return repository.findById(id)
                .map(mapper::toDoctorGetResponse)
                .orElseThrow(() -> {
                    log.warn("Doctor not found with id {}", id);

                    return new ResourceNotFoundException("Doctor not found");
                });
    }

    @Transactional(readOnly = true)
    public DoctorPageResponse findAll(Pageable pageable){
        var paginatedDoctors = repository.findAll(pageable);

        return mapper.toDoctorPageResponse(paginatedDoctors);
    }

}
