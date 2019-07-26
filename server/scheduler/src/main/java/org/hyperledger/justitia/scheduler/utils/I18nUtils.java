package org.hyperledger.justitia.scheduler.utils;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import sun.util.locale.LocaleUtils;

@Component
public class I18nUtils {
    private final MessageSource messageSource;

    public I18nUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String msgKey, Object[] args) {
        return messageSource.getMessage(msgKey, args, UserUtils.getLocale());
    }

    public String getMessage(String msgKey) {
        return messageSource.getMessage(msgKey, null, UserUtils.getLocale());
    }
}
