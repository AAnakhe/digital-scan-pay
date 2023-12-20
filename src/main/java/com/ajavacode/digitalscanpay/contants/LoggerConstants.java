package com.ajavacode.digitalscanpay.contants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerConstants {

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(LoggerConstants.class.getName() + "." + clazz.getSimpleName());
    }

    private LoggerConstants() {
    }
}
