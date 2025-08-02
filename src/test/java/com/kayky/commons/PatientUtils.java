package com.kayky.commons;

import com.kayky.domain.patient.Patient;
import com.kayky.enums.Gender;
import org.springframework.stereotype.Component;

@Component
public class PatientUtils {

    public static Patient patientToSave(){
        return Patient.builder()
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .gender(Gender.MALE)
                .address("101 Oak Lane, Newtown")
                .bloodType("A_NEGATIVE")
                .build();
    }
}

