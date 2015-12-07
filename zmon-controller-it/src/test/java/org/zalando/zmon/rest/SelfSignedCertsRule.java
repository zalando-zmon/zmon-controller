package org.zalando.zmon.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * To avoid handshake-exception when using the selfsigned cert.
 * 
 * @author jbellmann
 *
 */
public class SelfSignedCertsRule implements TestRule {
	
	private final Logger logger = Logger.getLogger(SelfSignedCertsRule.class.getName());

	private SSLSocketFactory defaultSslSocketFactory;
	private HostnameVerifier defaultHostnameVerifier;

	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				if(defaultSslSocketFactory == null){
					logger.info("save default-sslSocketFactory");
					defaultSslSocketFactory = SSLContext.getDefault().getSocketFactory();
				}
				if(defaultHostnameVerifier == null){
					logger.info("save default-hostnameVerifier");
					defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
				}
				
				logger.info("Disable cert-checking");
				//
				disableSslVerification();
				try {
					base.evaluate();
				}finally{
					logger.info("reset to defaults");
					HttpsURLConnection.setDefaultSSLSocketFactory(defaultSslSocketFactory);
					HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
				}
			}

		};

	}

	private  void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

}
