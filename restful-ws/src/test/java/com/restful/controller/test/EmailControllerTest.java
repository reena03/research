package com.restful.controller.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.restful.bean.Email;
import com.utils.HttpUtils;

public class EmailControllerTest {

	public static void main(String[] args) throws Exception {
		Email testemail = getSampleEmail();
		Gson gson = new Gson();
		String json = gson.toJson(testemail);
		String response = executeHttpRequestAndReturnEntityString("http://localhost:8080/ws/email", getHeaders(), json);

		System.out.println("response =" + response);

	}

	public static CloseableHttpResponse execute(String url, Map<String, String> header, String json) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try (CloseableHttpClient httpClient = HttpUtils.getHttpClient(1000)) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
			StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(requestEntity);
			response = httpClient.execute(httpPost);
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return response;
	}

	public static String executeHttpRequestAndReturnEntityString(String url, Map<String, String> headers, String json) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		httpPost.setEntity(requestEntity);

		CloseableHttpClient httpClient = HttpUtils.getHttpClient(6000);
		try (CloseableHttpResponse httpResponse = HttpUtils.executeHttpRequest(httpClient, httpPost, headers)) {
			if (httpResponse != null && httpResponse.getStatusLine() != null) {
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				HttpEntity responseEntity = httpResponse.getEntity();
				String responseString = StringUtils.EMPTY;
				if (responseEntity != null) {
					responseString = EntityUtils.toString(responseEntity, "UTF-8");
				}
				if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_CREATED || responseCode == HttpStatus.SC_NO_CONTENT) {
					if (responseEntity != null) {
						return responseString;
					}
					else {
						return StringUtils.EMPTY;
					}
				}
				else {
					throw new Exception("Error response: Response Code " + responseCode + ". Response Value: " + responseString);
				}
			}
		}
		throw new Exception("Http Request failed");
	}

	private static Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("x-api-key", "");
		headers.put("Content-type", ContentType.APPLICATION_JSON.toString());
		return headers;
	}

	public static Email getSampleEmail() {
		Email testEmail = new Email();
		testEmail.setFrom("reenasingh03@gmail.com");
		testEmail.setSubject("Having fun with coding");
		testEmail.setBody("Having fun with coding and testing and blah blah blah ... ");

		List<String> to = new ArrayList<>();
		List<String> cc = emailsBCC();
		List<String> bcc = new ArrayList<>();

		to.add("reenasingh03+test01@gmail.com");
		to.add("reenasingh03+test02@gmail.com");

		testEmail.setTo(to);
		testEmail.setCc(cc);
		return testEmail;
	}

	public static List<String> emailsTo() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("test10@example.com");
		emailtest.add("test20@example.com");
		return emailtest;
	}

	public static List<String> emailsCC() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("test11@example.com");
		return emailtest;
	}

	public static List<String> emailsBCC() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("test13@example.com");
		emailtest.add("test23@example.com");
		return emailtest;
	}
}
