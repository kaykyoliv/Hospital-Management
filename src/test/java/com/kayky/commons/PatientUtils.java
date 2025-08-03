package com.kayky.commons;

import com.kayky.domain.patient.Patient;
import com.kayky.domain.patient.response.PatientGetResponse;
import com.kayky.enums.Gender;

public final  class PatientUtils {

    private PatientUtils() {}

    private static Patient basePatient() {
        return Patient.builder()
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .active(true)
                .gender(Gender.MALE)
                .address("101 Oak Lane, Newtown")
                .bloodType("A_NEGATIVE")
                .build();
    }

    public static Patient patientToSave(){
        return basePatient();
    }

    public static Patient savedPatient(Long id) {
        return basePatient().toBuilder()
                .id(id)
                .build();
    }

    public static PatientGetResponse asGetResponse(Patient patient){
        return PatientGetResponse.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .bloodType(patient.getBloodType())
                .build();
    }
}

