package com.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

public class HttpUtils {
	//public static final Logger LOG = Logger.getLogger(HttpUtils.class);

	public static CloseableHttpClient getHttpClient(int timeoutInMilliSeconds) {
		RequestConfig config = RequestConfig.DEFAULT;
		if (timeoutInMilliSeconds > 0) {
			config = RequestConfig.custom()
					.setConnectTimeout(timeoutInMilliSeconds)
					.setConnectionRequestTimeout(timeoutInMilliSeconds)
					.setSocketTimeout(timeoutInMilliSeconds).build();
		}
		return HttpClientBuilder.create().setDefaultRequestConfig(config).build();

	}

	public static <T> T getResponseObject(CloseableHttpResponse response, Class<T> classType) throws IOException {
		if (response != null && classType != null) {
			int responseCode = response.getStatusLine().getStatusCode();
			String responseMessage = response.getStatusLine().getReasonPhrase();
			/*if (LOG.isDebugEnabled()) {
				LOG.debug("Response Code=" + responseCode + ". Message=" + responseMessage);
			}*/
			if (response.getEntity() != null && response.getEntity().getContent() != null) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); // TODO: Use EntityUtils.toString()
				Gson gson = new Gson();
				T responseObject = gson.fromJson(rd, classType);
				return responseObject;
			}
		}
		return null;
	}

	public static <T, U> T executeHttpRequestForSuccess(
			CloseableHttpClient httpClient,
			String url,
			HttpUriRequest request,
			Map<String, String> headers,
			Class<T> responseClassType) throws Exception {
		return executeHttpRequestForSuccess(httpClient, url, request, headers, responseClassType, null);
	}

	public static <T, U> T executeHttpRequestForSuccess(CloseableHttpClient httpClient,
			String url,
			HttpUriRequest request,
			Map<String, String> headers,
			Class<T> responseSuccessClassType,
			Class<U> responseFailureClassType) throws Exception {
		try (CloseableHttpResponse response = executeHttpRequest(httpClient, request, headers)) {
			return getSuccessResponse(url, responseSuccessClassType, responseFailureClassType, response);
		}
	}

	public static CloseableHttpResponse executeHttpRequest(CloseableHttpClient httpClient,
			HttpUriRequest request,
			Map<String, String> headers) throws IOException {
		if (headers != null) {
			for (String key : headers.keySet()) {
				request.addHeader(key, headers.get(key));
			}
		}
		CloseableHttpResponse response = httpClient.execute(request);
		return response;
	}

	private static <T, U> T getSuccessResponse(String url,
			Class<T> responseSuccessClassType,
			Class<U> responseFailureClassType,
			CloseableHttpResponse response) throws Exception {
		int responseCode = 0;
		U responseObject = null;
		if (response != null && response.getStatusLine() != null) {
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK) {
				return getResponseObject(response, responseSuccessClassType);
			}
			else {
				responseObject = getResponseObject(response, responseFailureClassType);
			}
		}
		throw new Exception("Invalid POST" + url);
	}

}
