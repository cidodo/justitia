package org.hyperledger.justitia.common.utils;

import org.springframework.context.MessageSource;

public class ParameterCheckUtils {
    private static MessageSource resources;

    public static void check(boolean condition, String errorMsg) {
        if (!condition) {
            fail(errorMsg);
        }
    }

    public static String notEmpty(String str, String errorMsg) {
        if (str == null || str.isEmpty()) {
            fail(errorMsg);
        }
        return str;
    }

    public static Object notNull(Object obj, String errorMsg) {
        if (obj == null) {
            fail(errorMsg);
        }
        return obj;
    }

    private static void fail(String errorMsg) {
        throw new IllegalArgumentException(errorMsg);
    }
}
