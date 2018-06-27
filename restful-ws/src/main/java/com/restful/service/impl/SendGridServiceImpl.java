package com.restful.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.restful.service.IEmailService;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@Service("sendGridService")
public class SendGridServiceImpl implements IEmailService {

	//private static final String API_KEY = "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";

	@Value("${sendGridService.apikey}")
	private String apiKey;

	@Override
	public String send(com.restful.bean.Email email) throws Exception {

		Mail mail = createSendGridMail(email);

		SendGrid sg = new SendGrid(apiKey);
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			if (response == null) {
				throw new Exception("No response");
			}
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			System.out.println(response.getHeaders());

			if (StringUtils.isNotBlank(response.getBody())) {
				return new Gson().toJson(response.getBody());
			}
			else if (HttpStatus.ACCEPTED.value() == response.getStatusCode()) {
				return "Email Sent Successfully";
			}
			else {
				return String.valueOf(response.getStatusCode());
			}
		} catch (IOException ex) {
			throw ex;
		}
	}

	/**
	 * This method converts email bean to sendgrid mail
	 * 
	 * @param email
	 * @return mail
	 */
	public Mail createSendGridMail(com.restful.bean.Email email) {

		Personalization personalization = new Personalization();
		Email from = new Email(email.getFrom());
		String subject = email.getSubject();
		Content content = new Content("text/plain", email.getBody());

		List<String> toEmail = email.getTo();
		List<String> ccEmail = email.getCc();
		List<String> bccEmail = email.getBcc();

		Mail mail = new Mail();
		mail.setFrom(from);
		mail.setSubject(subject);
		mail.addContent(content);

		if (CollectionUtils.isNotEmpty(toEmail)) {
			addEmailTo(toEmail, personalization);
		}
		if (CollectionUtils.isNotEmpty(ccEmail)) {
			addEmailCc(ccEmail, personalization);
		}

		if (CollectionUtils.isNotEmpty(bccEmail)) {
			addEmailBcc(bccEmail, personalization);
		}
		mail.addPersonalization(personalization);

		return mail;
	}

	private void addEmailTo(List<String> emails, Personalization personalization) {
		for (String emailId : emails) {
			Email email = new Email(emailId);
			personalization.addTo(email);
		}
	}

	private void addEmailCc(List<String> emails, Personalization personalization) {
		for (String emailId : emails) {
			Email email = new Email(emailId);
			personalization.addCc(email);
		}
	}

	private void addEmailBcc(List<String> emails, Personalization personalization) {
		for (String emailId : emails) {
			Email email = new Email(emailId);
			personalization.addBcc(email);
		}
	}

}
