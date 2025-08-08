package com.kayky.commons;

public final class TestConstants {

    private TestConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_ALREADY_EXIST = "Email %s already in use";

    public static final Long EXISTING_ID = 1L;
    public static final Long NON_EXISTING_ID = 999L;
}