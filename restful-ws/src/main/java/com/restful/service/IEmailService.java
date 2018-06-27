package com.restful.service;

import com.restful.bean.Email;

/**
 * This interface defines methods that need to be
 * implemented for an EmailService
 *
 */
public interface IEmailService {

	/**
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public String send(Email email) throws Exception;
}
