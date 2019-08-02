package org.hyperledger.justitia.common.utils;


import java.util.Collection;
import java.util.Iterator;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static String join(Collection<String> collection, String separator) {
        return collection == null ? null : join(collection.iterator(), separator);
    }

    public static String join(Iterator<String> iterator, String separator) {
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            String first = iterator.next();
            if (!iterator.hasNext()) {
                return first;
            } else {
                StringBuilder buf = new StringBuilder();
                if (first != null) {
                    buf.append(first);
                }

                while(iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    String str = iterator.next();
                    if (str != null) {
                        buf.append(str);
                    }
                }

                return buf.toString();
            }
        }
    }
}
