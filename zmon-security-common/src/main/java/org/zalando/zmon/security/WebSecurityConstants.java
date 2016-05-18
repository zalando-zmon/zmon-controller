package org.zalando.zmon.security;

public class WebSecurityConstants {

    public static String[] IGNORED_PATHS = new String[]{
            "/logo.png", "/favicon.ico", "/asset/**", "/styles/**", "/js/**", "/lib/**",
            "/grafana/**",
            "/grafana2/vendor/**", "/grafana2/img/**", "/grafana2/css/**", "/grafana2/fonts/**",
            "/tv/**"};
}
