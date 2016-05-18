package org.kairosdb.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;


/**
 * OAuth-enabled KairosDB HTTP Client
 */
public class KairosDBOAuthHttpClient extends AbstractClient {

    private CloseableHttpClient client;
    private int retries = 3;
    private String bearerToken;

    public KairosDBOAuthHttpClient(String url, CloseableHttpClient client, String bearerToken) throws MalformedURLException {
        super(url);
        this.client = client;
        this.bearerToken = bearerToken;
    }

    @Override
    protected ClientResponse postData(String json, String url) throws IOException {

        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost(url);
        if (bearerToken != null) {
            postMethod.setHeader("Authorization", "Bearer " + bearerToken);
        }
        postMethod.setEntity(requestEntity);

        return execute(postMethod);
    }

    @Override
    protected ClientResponse queryData(String url) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    protected ClientResponse delete(String url) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    private ClientResponse execute(HttpUriRequest request) throws IOException {
        HttpResponse response;

        int tries = ++retries;
        while (true) {
            tries--;
            try {
                response = client.execute(request);
                break;
            } catch (IOException e) {
                if (tries < 1)
                    throw e;
            }
        }

        return new HttpClientResponse(response);
    }

    @Override
    public void shutdown() throws IOException {
        client.close();
    }

    @Override
    public int getRetryCount() {
        return retries;
    }
}
