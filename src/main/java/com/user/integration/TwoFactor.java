package com.user.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.user.dto.response.TwoFactorResponse;
import com.user.entity.UserEntity;
import com.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TwoFactor {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserRepository userRepo;

	private static final String API_KEY = "20c96cd2-8056-11ec-b9b5-0200cd936042";

//	String sessionId = "5108d8ed-fc9a-4e22-9663-c88edaa597e0";
//	String otpInput = "941540";
//	String phoneNumber = "7003477547";

	public TwoFactorResponse sendingSmsOtp(String phoneNumber) {
		TwoFactorResponse twoFactorResponse = null;
		/* Sending SMS OTP */
		String sendingSmsOtpApi = "http://2factor.in/API/V1/" + API_KEY + "/SMS/" + phoneNumber + "/AUTOGEN";

		ResponseEntity<TwoFactorResponse> result = restTemplate.getForEntity(sendingSmsOtpApi, TwoFactorResponse.class);
		twoFactorResponse = result.getBody();
		if (twoFactorResponse != null) {
			if ("Success".equals(twoFactorResponse.getStatus())) {
				log.info("Sussess || twoFactorResponse: " + twoFactorResponse);

				/* save sessionId in user db */
				UserEntity userEntity = userRepo.findByMobile(phoneNumber).orElse(null);

				if (userEntity != null) {
					userEntity.setSessionId(twoFactorResponse.getDetails());
				}

			} else {
				log.info("Not Sussess ||" + twoFactorResponse.getStatus());
			}
		} else {
			log.info("TwoFactorResponse is null");
		}

		return twoFactorResponse;
	}

	public TwoFactorResponse verifySmsOtp(UserEntity userEntity, String otpInput) {
		TwoFactorResponse twoFactorResponse = null;
		/* Verify SMS OTP */
		String verifySmsOtpApi = "http://2factor.in/API/V1/" + API_KEY + "/SMS/VERIFY/" + userEntity.getSessionId()
				+ "/" + otpInput;

		ResponseEntity<TwoFactorResponse> result = restTemplate.getForEntity(verifySmsOtpApi, TwoFactorResponse.class);
		twoFactorResponse = result.getBody();
		log.info("Sending SMS OTP twoFactorResponse : " + twoFactorResponse);

		if (twoFactorResponse != null) {
			if ("Success".equals(twoFactorResponse.getStatus())) {
				log.info("Sussess || twoFactorResponse: " + twoFactorResponse);

				/* save verified in user db */

				if (userEntity != null) {
					userEntity.setVerified(Boolean.TRUE);
				}
			} else {
				log.info("Not Sussess ||" + twoFactorResponse.getStatus());
			}
		} else {
			log.info("TwoFactorResponse is null");
		}
		return twoFactorResponse;
	}
}
