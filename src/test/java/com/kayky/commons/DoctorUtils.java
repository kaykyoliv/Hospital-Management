package com.kayky.commons;

import com.kayky.domain.doctor.Doctor;
import com.kayky.domain.doctor.request.DoctorBaseRequest;
import com.kayky.domain.doctor.response.DoctorBaseResponse;
import com.kayky.domain.user.enums.Gender;

import java.util.Arrays;
import java.util.List;

public final class DoctorUtils {

    private DoctorUtils() {
    }

    private static Doctor baseDoctor() {
        return Doctor.builder()
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .active(true)
                .gender(Gender.MALE)
                .specialty("Cardiology")
                .crm("CRM-12345")
                .phoneNumber("+55 19 99999-0001")
                .officeNumber("Room 101")
                .availability(true)
                .build();
    }


    public static Doctor savedDoctor(Long id) {
        return baseDoctor().toBuilder()
                .id(id)
                .build();
    }


    public static Doctor updatedDoctor() {
        return baseDoctor().toBuilder()
                .id(1L)
                .firstName("Robert updated")
                .lastName("Williams updated")
                .build();
    }


    public static DoctorBaseResponse asBaseResponse(Doctor doctor) {
        return DoctorBaseResponse.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .gender(doctor.getGender())
                .specialty(doctor.getSpecialty())
                .crm(doctor.getCrm())
                .phoneNumber(doctor.getPhoneNumber())
                .officeNumber(doctor.getOfficeNumber())
                .availability(doctor.getAvailability())
                .build();
    }

    public static List<Doctor> doctorList() {
        Doctor doctor1 = Doctor.builder()
                .id(1L)
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .active(true)
                .gender(Gender.MALE)
                .specialty("Cardiology")
                .crm("CRM-12345")
                .phoneNumber("+55 19 99999-0001")
                .officeNumber("Room 101")
                .availability(true)
                .build();

        Doctor doctor2 = Doctor.builder()
                .id(2L)
                .firstName("Emily")
                .lastName("Johnson")
                .email("emily.johnson@example.com")
                .password("hashed_password_5")
                .active(true)
                .gender(Gender.FEMALE)
                .specialty("Dermatology")
                .crm("CRM-67890")
                .phoneNumber("+55 11 98888-0002")
                .officeNumber("Room 102")
                .availability(true)
                .build();

        Doctor doctor3 = Doctor.builder()
                .id(3L)
                .firstName("Michael")
                .lastName("Brown")
                .email("michael.brown@example.com")
                .password("hashed_password_6")
                .active(true)
                .gender(Gender.MALE)
                .specialty("Neurology")
                .crm("CRM-54321")
                .phoneNumber("+55 21 97777-0003")
                .officeNumber("Room 103")
                .availability(false)
                .build();

        return Arrays.asList(doctor1, doctor2, doctor3);
    }

    public static List<DoctorBaseResponse> asBaseResponseList(){
       return  doctorList().stream()
               .map(DoctorUtils::asBaseResponse)
               .toList();
    }

    public static DoctorBaseRequest asBaseRequest() {
        return DoctorBaseRequest.builder()
                .firstName("Robert")
                .lastName("Williams")
                .email("robert.williams@example.com")
                .password("hashed_password_4")
                .gender(Gender.MALE)
                .specialty("Cardiology")
                .crm("CRM-12345")
                .phoneNumber("+55 19 99999-0001")
                .officeNumber("Room 101")
                .availability(true)
                .build();
    }

}

