package org.zalando.zmon.security;

public class WebSecurityConstants {

    public static String[] IGNORED_PATHS = new String[]{
            "/logo.png","/clean.png","/warning.png", "/favicon.ico", "/asset/**", "/styles/**", "/js/**", "/lib/**",
            "/grafana/public/**"};
}
