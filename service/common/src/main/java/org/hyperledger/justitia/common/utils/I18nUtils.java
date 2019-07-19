package org.hyperledger.justitia.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18nUtils {
    private static MessageSource messageSource;

    public I18nUtils(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
    }

    public static String getMessage(String msgKey, Object[] args, Locale locale) {
        return messageSource.getMessage(msgKey, args, locale);
    }

    public static String getMessage(String msgKey, Locale locale) {
        return messageSource.getMessage(msgKey, null, locale);
    }
}
