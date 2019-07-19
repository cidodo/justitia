package org.hyperledger.justitia.scheduler.utils;

import org.hyperledger.justitia.scheduler.exception.UnloginException;

import java.util.Locale;

public class UserUtils {
    private static final ThreadLocal<String> tlUser = new ThreadLocal<>();
    private static final ThreadLocal<Locale> tlLocale = ThreadLocal.withInitial(() -> Locale.CHINESE);

    public static void setUser(String userid) {
        tlUser.set(userid);
    }

    /**
     * 如果没有登录，返回null
     *
     * @return
     */
    public static String getUserIfLogin() {
        return tlUser.get();
    }

    /**
     * 如果没有登录会抛出异常
     */
    public static String getUser() {
        String user = tlUser.get();

        if (user == null) {
            throw new UnloginException();
        }

        return user;
    }

    public static void setLocale(String locale) {
        setLocale(new Locale(locale));
    }

    public static void setLocale(Locale locale) {
        tlLocale.set(locale);
    }

    public static Locale getLocale() {
        return tlLocale.get();
    }

    public static void clearAllUserInfo() {
        tlUser.remove();
        tlLocale.remove();
    }
}
