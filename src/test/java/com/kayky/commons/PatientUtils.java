package com.kayky.commons;

import com.kayky.domain.patient.Patient;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import com.kayky.domain.user.enums.Gender;

import java.util.List;

public final class PatientUtils {

    private PatientUtils() {
    }

    private static Patient basePatient() {
        return Patient.builder()
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .active(true)
                .gender(Gender.MALE)
                .address("101 Oak Lane, Newtown")
                .bloodType("AB-")
                .build();
    }

    public static List<Patient> patientList() {
        var patient1 = Patient.builder()
                .id(1L)
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .active(true)
                .gender(Gender.MALE)
                .address("101 Oak Lane, Newtown")
                .bloodType("AB-")
                .build();

        var patient2 = Patient.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("hashed_password_2")
                .active(true)
                .gender(Gender.FEMALE)
                .address("456 Oak Ave, Somecity")
                .bloodType("AB-")
                .build();

        var patient3 = Patient.builder()
                .id(3L)
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .password("hashed_password_3")
                .active(false)
                .gender(Gender.FEMALE)
                .address("789 Pine Rd, Othercity")
                .bloodType("AB-")
                .build();

        return List.of(patient1, patient2, patient3);
    }

    public static Patient patientToSave() {
        return basePatient();
    }

    public static Patient updatedPatient() {
        return basePatient().toBuilder()
                .id(1L)
                .firstName("Robert updated")
                .lastName("Williams updated")
                .email("update@gmail.com")
                .build();
    }

    public static Patient savedPatient(Long id) {
        return basePatient().toBuilder()
                .id(id)
                .build();
    }


    public static List<PatientBaseResponse> asBaseResponseList() {
        return patientList().stream()
                .map(PatientUtils::asBaseResponse)
                .toList();
    }

    public static PatientBaseResponse asBaseResponse(Patient patient) {
        return PatientBaseResponse.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .bloodType(patient.getBloodType())
                .build();
    }

    public static PatientBaseRequest asBaseRequest() {
        return PatientBaseRequest.builder()
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .gender(Gender.MALE)
                .address("101 Oak Lane, Newtown")
                .bloodType("AB-")
                .build();
    }

}

