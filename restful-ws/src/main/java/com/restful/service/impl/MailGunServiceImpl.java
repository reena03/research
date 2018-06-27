package com.restful.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.restful.bean.Email;
import com.restful.service.IEmailService;

@Service("mailGunService")
public class MailGunServiceImpl implements IEmailService {

	//private static final String URL = "https://api.mailgun.net/v3/sandbox4607f08ee4564e7ca5f563602ca146c0.mailgun.org/messages";

	@Value("${mailGunService.apikey}")
	private String apiKey;

	@Value("${mailGunService.url}")
	private String url;

	/* (non-Javadoc)
	 * @see com.restful.service.IEmailService#send(com.restful.bean.Email)
	 */
	@Override
	public String send(Email email) throws Exception {
		Map<String, Object> emails = getEmailMap(email);

		HttpResponse<JsonNode> request = Unirest.post(url)
				.basicAuth("api", apiKey)
				.queryString("from", email.getFrom())
				.queryString(emails)
				.queryString("subject", email.getSubject())
				.queryString("text", email.getBody())
				.asJson();

		if (request == null) {
			throw new Exception("No response");
		}
		System.out.println(request.getBody());
		System.out.println(request.getStatus());
		Gson gson = new Gson();
		return gson.toJson(request.getBody());
	}

	/**
	 * @param email
	 * @return
	 */
	private Map<String, Object> getEmailMap(Email email) {
		String toList = joinEmails(email.getTo());
		String ccList = joinEmails(email.getCc());
		String bccList = joinEmails(email.getBcc());

		Map<String, Object> emails = new HashMap<>();
		if (StringUtils.isNotBlank(toList)) {
			emails.put("to", toList);
		}

		if (StringUtils.isNotBlank(ccList)) {
			emails.put("cc", toList);
		}

		if (StringUtils.isNotBlank(bccList)) {
			emails.put("bcc", toList);
		}
		return emails;
	}

	/**
	 * @param emails
	 * @return
	 */
	private String joinEmails(List<String> emails) {
		if (CollectionUtils.isNotEmpty(emails)) {
			return StringUtils.join(emails, ',');
		}
		return "";
	}

}
