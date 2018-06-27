package com.restful.service.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class EmailTest {

	private static String SENDGRID_API_KEY = null;
	private static String MAILGUN_API_KEY = null;
	private static String MAILGUN_URL = null;

	public static void main(String[] args) throws Exception {

		SENDGRID_API_KEY = args[0];
		MAILGUN_API_KEY = args[1];
		MAILGUN_URL = args[2];

		testSendGridn();
		testSendGridPersonalization();
		JsonNode response = sendSimpleMessage();
		System.out.println(response.getObject());

	}

	public static List<String> emailsTo() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("reenasingh03@gmail.com");
		//emailtest.add("test20@example.com");
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

	public static void testSendGridPersonalization() throws IOException {
		Mail mail = new Mail();
		Email from = new Email("test@gmail.com");
		String subject = "Sending with SendGrid is Fun";
		Content content = new Content("text/plain", "and easy to do anywhere, even with Java");

		mail.setFrom(from);
		mail.setSubject(subject);
		mail.addContent(content);

		Personalization personalization = new Personalization();
		addEmailTo(emailsTo(), personalization);
		/*addEmailCc(emailsCC(), personalization);
		addEmailBcc(emailsBCC(), personalization);*/
		mail.addPersonalization(personalization);

		SendGrid sg = new SendGrid(SENDGRID_API_KEY);
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println(" testSendGridPersonalization ==" + response.getStatusCode());
			System.out.println(" testSendGridPersonalization == " + response.getBody());
			System.out.println(" testSendGridPersonalization == " + response.getHeaders());
		} catch (IOException ex) {
			throw ex;
		}
	}

	public static void addEmailTo(List<String> emails, Personalization personalization) {

		for (String emailId : emails) {
			Email email = new Email(emailId);
			personalization.addTo(email);
		}

	}

	public static void addEmailCc(List<String> emails, Personalization personalization) {

		for (String emailId : emails) {
			Email email = new Email(emailId);
			personalization.addCc(email);
		}

	}

	public static void addEmailBcc(List<String> emails, Personalization personalization) {

		for (String emailId : emails) {
			Email email = new Email(emailId);
			personalization.addBcc(email);
		}

	}

	public static void testSendGridn() throws IOException {
		Email from = new Email("reenasingh03@gmail.com");
		String subject = "Sending with SendGrid is Fun";
		Email to = new Email("ajitsharma@gmail.com");
		Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
		Mail mail = new Mail(from, subject, to, content);

		SendGrid sg = new SendGrid(SENDGRID_API_KEY);
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			System.out.println(response.getHeaders());
		} catch (IOException ex) {
			throw ex;
		}

	}

	public static JsonNode sendSimpleMessage() throws UnirestException {

		String toList = getCommaSeparatedEmailString(emailsTo());
		String ccList = getCommaSeparatedEmailString(emailsCC());
		String bccList = getCommaSeparatedEmailString(null);

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

		HttpResponse<JsonNode> request = Unirest.post(MAILGUN_URL)
				.basicAuth("api", MAILGUN_API_KEY)
				.queryString("from", "User <USER@YOURDOMAIN.COM>")
				.queryString(emails)
				.queryString("subject", "hello")
				.queryString("text", "testing")
				.asJson();

		return request.getBody();
	}

	private static String getCommaSeparatedEmailString(List<String> emails) {
		if (emails != null && CollectionUtils.isNotEmpty(emails)) {
			return StringUtils.join(emails, ',');
		}
		return "";
	}

}
