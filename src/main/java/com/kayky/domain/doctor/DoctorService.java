package com.kayky.domain.doctor;

import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.doctor.request.DoctorBaseRequest;
import com.kayky.domain.doctor.response.DoctorBaseResponse;
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
public class DoctorService {

    private final DoctorRepository repository;
    private final DoctorMapper mapper;
    private final UserValidator userValidator;

    @Transactional(readOnly = true)
    public DoctorBaseResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDoctorBaseResponse)
                .orElseThrow(() -> {
                    log.warn("Doctor not found with id {}", id);

                    return new ResourceNotFoundException("Doctor not found");
                });
    }

    @Transactional(readOnly = true)
    public PageResponse<DoctorBaseResponse> findAll(Pageable pageable) {
        var paginatedDoctors = repository.findAll(pageable);
        return PageUtils.mapPage(paginatedDoctors, mapper::toDoctorBaseResponse);
    }

    @Transactional
    public DoctorBaseResponse save(DoctorBaseRequest request) {
        userValidator.assertEmailDoesNotExist(request.getEmail());

        var doctorToSave = mapper.toEntity(request);
        var savedDoctor = repository.save(doctorToSave);

        log.info("New doctor saved with ID {}", savedDoctor.getId());

        return mapper.toDoctorBaseResponse(savedDoctor);
    }

    @Transactional
    public DoctorBaseResponse update(DoctorBaseRequest request, Long id) {
        var doctorToUpdate = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: Doctor not found with ID {}", id);
                    return new ResourceNotFoundException("Doctor not found");
                });

        userValidator.assertEmailDoesNotExist(request.getEmail(), id);

        mapper.updateDoctorFromRequest(request, doctorToUpdate);

        var updatedDoctor = repository.save(doctorToUpdate);

        return mapper.toDoctorBaseResponse(updatedDoctor);
    }

}
