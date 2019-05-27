package org.zalando.zmon.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestExecution;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;

import java.io.IOException;

public class CorsHeadersCleanerInterceptors implements AsyncClientHttpRequestInterceptor {
    @Override
    public ListenableFuture<ClientHttpResponse> intercept(HttpRequest httpRequest, byte[] bytes, AsyncClientHttpRequestExecution asyncClientHttpRequestExecution) throws IOException {
        ListenableFuture<ClientHttpResponse> listenableFuture = asyncClientHttpRequestExecution.executeAsync(httpRequest, bytes);
        return new ListenableFutureAdapter<ClientHttpResponse, ClientHttpResponse>(listenableFuture) {
            @Override
            protected ClientHttpResponse adapt(ClientHttpResponse clientHttpResponse) {
                clientHttpResponse.getHeaders().remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                clientHttpResponse.getHeaders().remove(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                clientHttpResponse.getHeaders().remove(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                clientHttpResponse.getHeaders().remove(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
                return clientHttpResponse;
            }
        };
    }
}
