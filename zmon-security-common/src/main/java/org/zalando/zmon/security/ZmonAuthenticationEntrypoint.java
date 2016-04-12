package org.zalando.zmon.security;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author jbellmann
 *
 */
public class ZmonAuthenticationEntrypoint extends LoginUrlAuthenticationEntryPoint {

    private static final Set<MediaType> ACCEPT_TYPES_MATCHES = Sets.newHashSet(MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_JSON_UTF8);

    private final Set<MediaType> matches;

    public ZmonAuthenticationEntrypoint(String loginFormUrl) {
        this(loginFormUrl, ACCEPT_TYPES_MATCHES);
    }

    public ZmonAuthenticationEntrypoint(String loginFormUrl, Set<MediaType> mediaTypes) {
        super(loginFormUrl);
        matches = mediaTypes;

    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        Optional<MediaType> mediaType = resolveMediaType(request);
        if (mediaType.isPresent() && matches.contains(mediaType.get())) {
            if (!response.isCommitted()) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter()
                        .write("{\"message\":\"" + StringEscapeUtils.escapeJson(authException.getMessage()) + "\"}");
            }
        } else {
            super.commence(request, response, authException);
        }
    }

    protected Optional<MediaType> resolveMediaType(HttpServletRequest request) {
        Optional<String> acceptHeader = resolveAcceptHeader(request);
        if (acceptHeader.isPresent()) {
            try {
                MediaType mediaType = MediaType.parseMediaType(acceptHeader.get());
                return Optional.ofNullable(mediaType);
            } catch (InvalidMediaTypeException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    protected Optional<String> resolveAcceptHeader(HttpServletRequest request) {
        Set<String> headers = StringUtils.commaDelimitedListToSet(request.getHeader(HttpHeaders.ACCEPT));
        return Optional.ofNullable(Iterables.getFirst(headers, null));
    }

}
