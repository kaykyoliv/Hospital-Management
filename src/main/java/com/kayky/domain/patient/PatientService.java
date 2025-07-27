    package com.kayky.domain.patient;

    import com.kayky.domain.patient.request.PatientPostRequest;
    import com.kayky.domain.patient.request.PatientPutRequest;
    import com.kayky.domain.patient.response.PatientGetResponse;
    import com.kayky.domain.patient.response.PatientPostResponse;
    import com.kayky.domain.patient.response.PatientPutResponse;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.server.ResponseStatusException;

    @Service
    @RequiredArgsConstructor
    public class PatientService {

        private final PatientRepository patientRepository;
        private final PatientMapper patientMapper;

        @Transactional(readOnly = true)
        public PatientGetResponse findById(Long id) {
            var patient = patientRepository.findById(id).
                    orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            return patientMapper.toPatientGetResponse(patient);
        }

        @Transactional(readOnly = true)
        public Page<PatientGetResponse> findAll(Pageable pageable) {
            var paginatedPatients = patientRepository.findAll(pageable);
            return patientMapper.toPageGetResponse(paginatedPatients);
        }

        @Transactional
        public PatientPostResponse save(PatientPostRequest postRequest) {
            var patientToSave = patientMapper.toEntity(postRequest);
            assertEmailDoesNotExist(postRequest.getEmail());
            var patientSaved = patientRepository.save(patientToSave);
            return patientMapper.toPatientPostResponse(patientSaved);
        }

        @Transactional
        public PatientPutResponse update(PatientPutRequest putRequest, Long id) {
            var patientToUpdate = patientRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            assertEmailDoesNotExist(putRequest.getEmail(), id);

            patientMapper.updatePatientFromRequest(putRequest, patientToUpdate);
            var updatedPatient = patientRepository.save(patientToUpdate);
            return patientMapper.toPatientPutResponse(updatedPatient);
        }

        private void assertEmailDoesNotExist(String email) {
            patientRepository.findByEmail(email).ifPresent(this::throwEmailExistsException);
        }

        private void assertEmailDoesNotExist(String email, Long id) {
            patientRepository.findByEmailAndIdNot(email, id).ifPresent(this::throwEmailExistsException);
        }

        private void throwEmailExistsException(Patient patient) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

    }
