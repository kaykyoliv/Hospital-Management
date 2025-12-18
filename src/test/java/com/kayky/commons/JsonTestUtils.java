package com.kayky.commons;

import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;

public final class JsonTestUtils {

    private JsonTestUtils() {
    }

    public static void assertJsonEquals(String actual, String expected, String... ignoredPaths) {
        var assertion = JsonAssertions.assertThatJson(actual);

        if (ignoredPaths != null && ignoredPaths.length > 0) {
            assertion = assertion.whenIgnoringPaths(ignoredPaths);
        }

        assertion
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expected);
    }
}
