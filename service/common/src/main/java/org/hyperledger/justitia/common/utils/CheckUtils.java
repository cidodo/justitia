package org.hyperledger.justitia.common.utils;

import org.springframework.context.MessageSource;

public class CheckUtils {
    private static MessageSource resources;

    public static void check(boolean condition, String errorMsg) {
        if (!condition) {
            fail(errorMsg);
        }
    }

    public static void notEmpty(String str, String errorMsg) {
        if (str == null || str.isEmpty()) {
            fail(errorMsg);
        }
    }

    public static void notNull(Object obj, String errorMsg) {
        if (obj == null) {
            fail(errorMsg);
        }
    }

    private static void fail(String errorMsg) {
        throw new IllegalArgumentException(errorMsg);
    }
}
