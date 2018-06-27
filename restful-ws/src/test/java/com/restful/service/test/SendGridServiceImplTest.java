package com.restful.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.restful.bean.Email;
import com.restful.service.impl.SendGridServiceImpl;
import com.sendgrid.Mail;
import com.sendgrid.Personalization;

@RunWith(MockitoJUnitRunner.class)
public class SendGridServiceImplTest {

	SendGridServiceImpl sendGridService;

	@Before
	public void setup() {
		sendGridService = new SendGridServiceImpl();
	}

	@Test
	public void createSendGridMail_shouldAddPersonalizationWhenCC() throws Exception {
		Mail mail = sendGridService.createSendGridMail(getSampleEmail());
		List<Personalization> personalizations = mail.getPersonalization();
		assertNotNull(personalizations);
		assertEquals(1, personalizations.size());
		assertNotNull(personalizations.get(0).getCcs());
		assertNotNull(personalizations.get(0).getBccs());
	}

	@Test
	public void createSendGridMail_shouldNotAddPersonalizationWhenNoCCNoBccAndSingleToEmail() throws Exception {
		Email email = getSampleEmail();
		email.setCc(null);
		email.setBcc(null);
		email.setTo(emailsToWithSingleEmail());
		Mail mail = sendGridService.createSendGridMail(email);
		List<Personalization> personalizations = mail.getPersonalization();
		assertNull(personalizations.get(0).getCcs());
		assertNull(personalizations.get(0).getBccs());

	}

	@Test(expected = Exception.class)
	public void send_shouldNotSendWhenNoApiKey() throws Exception {
		Email email = getSampleEmail();

		try {
			String response = sendGridService.send(email);
		} catch (Exception e) {
			throw e;
		}

	}

	public Email getSampleEmail() {
		Email testEmail = new Email();
		testEmail.setFrom("reenasingh03@gmail.com");
		testEmail.setSubject("Having fun with coding");
		testEmail.setBody("Having fun with coding and testing and blah blah blah ... ");

		List<String> to = new ArrayList<>();
		List<String> cc = emailsBCC();
		List<String> bcc = emailsBCC();

		to.add("reenasingh03+test01@gmail.com");
		to.add("reenasingh03+test02@gmail.com");

		testEmail.setTo(to);
		testEmail.setCc(cc);
		testEmail.setBcc(bcc);
		return testEmail;
	}

	public List<String> emailsToWithSingleEmail() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("test10@example.com");
		return emailtest;
	}

	public List<String> emailsTo() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("test10@example.com");
		emailtest.add("test20@example.com");
		return emailtest;
	}

	public List<String> emailsCC() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("test11@example.com");
		return emailtest;
	}

	public List<String> emailsBCC() {
		List<String> emailtest = new ArrayList<>();
		emailtest.add("test13@example.com");
		emailtest.add("test23@example.com");
		return emailtest;
	}
}
