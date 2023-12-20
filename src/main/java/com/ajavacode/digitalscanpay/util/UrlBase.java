package com.ajavacode.digitalscanpay.util;

public class UrlBase {
    public static String url(String url){
        return String.format("/api/v1/%s",url);
    }
}
