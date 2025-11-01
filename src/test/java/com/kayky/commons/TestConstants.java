package com.kayky.commons;

public final class TestConstants {

    private TestConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String DOCTOR_NOT_FOUND = "Doctor not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String OPERATION_NOT_FOUND = "Operation not found";
    public static final String REPORT_NOT_FOUND = "Report not found";
    public static final String EMAIL_ALREADY_EXISTS = "Email %s already in use";
    public static final String USER_NOT_FOUND_SAVE_OPERATION = "%s with id %d not found";
    public static final String REPORT_ALREADY_EXISTS = "Report already exists for operation with ID: %d";

    public static final Long EXISTING_ID = 1L;
    public static final Long NON_EXISTING_ID = 999L;
}