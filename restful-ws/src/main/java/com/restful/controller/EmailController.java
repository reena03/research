package com.restful.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.restful.bean.Email;
import com.restful.error.ApiInvocationException;
import com.restful.service.IEmailService;
import com.restful.service.impl.EmailUtilService;

@RestController
public class EmailController {

	@Autowired
	@Qualifier(value = "sendGridService")
	private IEmailService sendGridService;

	@Autowired
	@Qualifier(value = "mailGunService")
	private IEmailService mailGunService;

	@Autowired
	private EmailUtilService emailUtilService;

	@RequestMapping(path = "/ws/email", method = RequestMethod.POST)
	public ResponseEntity<String> email(@RequestBody Email email) throws Exception {

		// Check if email object has valid email recipients
		emailUtilService.validateEmailObject(email);

		String responeString = null;

		try {
			responeString = invokeSend(email, mailGunService);
		} catch (Exception e) {
			responeString = invokeSend(email, sendGridService);
		}
		return new ResponseEntity<>(responeString, org.springframework.http.HttpStatus.ACCEPTED);
	}

	private String invokeSend(Email email, IEmailService emailService) throws ApiInvocationException {
		String responeString = null;
		try {
			responeString = emailService.send(email);
			if (StringUtils.isBlank(responeString)) {
				throw new ApiInvocationException("No response received");
			}
		} catch (Exception e) {
			throw new ApiInvocationException("Unexpected error on invoking api ");
		}

		return responeString;
	}

}
