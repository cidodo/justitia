package org.hyperledger.justitia.common;

import org.hyperledger.justitia.common.exception.ContextException;

import java.util.Locale;

public class RequestContext {
    private final static ThreadLocal<String> organizationId = new ThreadLocal<>();
    private final static ThreadLocal<String> user = new ThreadLocal<>();
    private final static ThreadLocal<Locale> locale = ThreadLocal.withInitial(() -> Locale.CHINESE);

    public static void setOrganizationId(String organizationId) {
        RequestContext.organizationId.set(organizationId);
    }

    public static String getOrganizationId() {
        String organizationId = RequestContext.organizationId.get();
        if (null == organizationId) {
            //todo
            throw new ContextException("");
        }
        return organizationId;
    }

    public static void setUser(String userId) {
        user.set(userId);
    }

    public static String getUser() {
        String userId = user.get();
        if (userId == null) {
            //todo
            throw new ContextException("");
        }
        return userId;
    }

    public static void setLocale(String locale) {
        setLocale(new Locale(locale));
    }

    public static void setLocale(Locale locale) {
        RequestContext.locale.set(locale);
    }

    public static Locale getLocale() {
        return locale.get();
    }


    public static void clearAllInfo() {
        organizationId.remove();
        user.remove();
        locale.remove();
    }
}
