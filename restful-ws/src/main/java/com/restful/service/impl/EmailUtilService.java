package com.restful.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.restful.bean.Email;
import com.restful.error.InvalidRequestException;
import com.utils.HttpUtils;

@Service
public class EmailUtilService {

	public void validateEmailObject(Email email) throws InvalidRequestException {

		List<String> combinedEmails = new ArrayList<>();

		if (email == null || CollectionUtils.isEmpty(email.getTo())) {
			throw new InvalidRequestException("No recipients specified");
		}

		// For simplicity if any of the email address in to, cc and bcc are not valid return error message
		if (!isValid(email.getTo())) {
			throw new InvalidRequestException("Invalid emails found  ");
		}
		if (CollectionUtils.isNotEmpty(email.getTo())) {
			if (hasDuplicate(email.getTo())) {
				throw new InvalidRequestException("Duplicate emails not allowed in to: reciplient list  ");
			}
			combinedEmails.addAll(email.getTo());
		}

		// Only check cc if present
		if (CollectionUtils.isNotEmpty(email.getCc())) {
			if (!isValid(email.getCc())) {
				throw new InvalidRequestException("Invalid emails found  ");
			}
			if (hasDuplicate(email.getCc())) {
				throw new InvalidRequestException("Duplicate emails not allowed in cc: reciplient list  ");
			}
			combinedEmails.addAll(email.getCc());
		}

		// Only check bcc if present 
		if (CollectionUtils.isNotEmpty(email.getBcc())) {
			if (!isValid(email.getBcc())) {
				throw new InvalidRequestException("Invalid emails found  ");
			}
			if (hasDuplicate(email.getBcc())) {
				throw new InvalidRequestException("Duplicate emails not allowed in bcc: reciplient list  ");
			}
			combinedEmails.addAll(email.getBcc());
		}
		// Check that there are no duplicates across all to,cc and bcc
		if (CollectionUtils.isNotEmpty(combinedEmails)) {
			if (hasDuplicate(combinedEmails)) {
				throw new InvalidRequestException("Duplicate emails not allowed  ");
			}
		}
	}

	/**
	 * Basic validation.
	 * 
	 * TODO add more validation if time permits
	 *
	 * @param email
	 * @return
	 */
	private boolean isValid(List<String> emailList) {
		for (String emailId : emailList) {
			if (StringUtils.isNotBlank(emailId)) {
				if (!EmailValidator.getInstance().isValid(emailId)) {
					return false;
				}
			}
			else {
				return false;
			}
		}

		return true;
	}

	public boolean hasDuplicate(List<String> emails) {
		Set<String> set = new HashSet<>();
		for (String emailId : emails) {
			if (!set.add(emailId)) {
				return true;
			}
		}
		return false;
	}

	public CloseableHttpResponse execute(String url, Map<String, String> header, String json) throws IOException {
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

	public String executeHttpRequestAndReturnEntityString(String url, Map<String, String> headers, String json) throws Exception {
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

	public Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-type", ContentType.APPLICATION_JSON.toString());
		return headers;
	}

}
