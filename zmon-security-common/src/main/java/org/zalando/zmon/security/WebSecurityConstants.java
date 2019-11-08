package org.zalando.zmon.security;

public class WebSecurityConstants {

    public static String[] IGNORED_PATHS = new String[]{
            "/logo.svg","/clean.png","/warning.png", "/favicon-16x16.png", "/favicon-32x32.png", "/asset/**", "/styles/**", "/js/**", "/lib/**",
            "/grafana/public/**",
            "/tv/**"};
}
