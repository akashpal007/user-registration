package com.user.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.dto.response.TwoFactorResponse;
import com.user.entity.PartnerEntity;
import com.user.entity.UserEntity;
import com.user.exception.GenericException;
import com.user.exception.TwoFactorException;
import com.user.repository.PartnerRepository;
import com.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TwoFactor {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PartnerRepository partnerRepo;

	private static final String PARTNER_TWO_FACTOR = "2Factor";

	private static final String TWO_FACTOR_BASE_URL = "http://2factor.in/API/V1/";

	public TwoFactorResponse sendingSmsOtp(String phoneNumber) throws GenericException, JsonProcessingException {
		TwoFactorResponse twoFactorResponse = null;
		/* Get API key from DB */
		String apiKey = getApiKey();
		/* Sending SMS OTP */
		String sendingSmsOtpApi = TWO_FACTOR_BASE_URL + apiKey + "/SMS/" + phoneNumber + "/AUTOGEN";
		ResponseEntity<TwoFactorResponse> result = null;
		try {
			result = restTemplate.getForEntity(sendingSmsOtpApi, TwoFactorResponse.class);
			twoFactorResponse = result.getBody();

		} catch (Exception e) {
			String exceptionMsg = e.getMessage();
			log.info("2factor verify OTP error" + exceptionMsg);
			String exceptionJsonInString = exceptionMsg.substring(exceptionMsg.indexOf("{"),
					exceptionMsg.lastIndexOf("}") + 1);
			twoFactorResponse = new ObjectMapper().readValue(exceptionJsonInString, TwoFactorResponse.class);
			log.info("Sending SMS OTP Fail twoFactorResponse : " + twoFactorResponse);
		}

		if (twoFactorResponse != null) {
			if ("Success".equals(twoFactorResponse.getStatus())) {
				log.info("Sussess || twoFactorResponse: " + twoFactorResponse);

				/* save sessionId in user db */
				UserEntity userEntity = userRepo.findByMobile(phoneNumber).orElse(null);

				if (userEntity != null) {
					userEntity.setSessionId(twoFactorResponse.getDetails());
					userRepo.save(userEntity);
				}

			} else {
				log.info("Not Sussess ||" + twoFactorResponse.getStatus());
				throw new GenericException("Something went wrong. Please try again after sometime");
			}
		} else {
			log.info("TwoFactorResponse is null");
			throw new GenericException("Something went wrong. Please try again after sometime");
		}

		return twoFactorResponse;
	}

	public TwoFactorResponse verifySmsOtp(UserEntity userEntity, String otpInput)
			throws GenericException, TwoFactorException, JsonProcessingException {
		TwoFactorResponse twoFactorResponse = null;
		/* Get API key from DB */
		String apiKey = getApiKey();
		/* Verify SMS OTP */
		String verifySmsOtpApi = TWO_FACTOR_BASE_URL + apiKey + "/SMS/VERIFY/" + userEntity.getSessionId() + "/"
				+ otpInput;

		ResponseEntity<TwoFactorResponse> result = null;
		try {
			result = restTemplate.getForEntity(verifySmsOtpApi, TwoFactorResponse.class);
			twoFactorResponse = result.getBody();
			log.info("Sending SMS OTP Success twoFactorResponse : " + twoFactorResponse);
		} catch (Exception e) {
			String exceptionMsg = e.getMessage();
			log.info("2factor verify OTP error" + exceptionMsg);
			String exceptionJsonInString = exceptionMsg.substring(exceptionMsg.indexOf("{"),
					exceptionMsg.lastIndexOf("}") + 1);
			twoFactorResponse = new ObjectMapper().readValue(exceptionJsonInString, TwoFactorResponse.class);
			log.info("Sending SMS OTP Fail twoFactorResponse : " + twoFactorResponse);
		}

		if (twoFactorResponse != null) {
			if ("Success".equals(twoFactorResponse.getStatus())) {
				/*
				 * OTP Matched
				 * OTP Expired
				 */
				
				log.info("Sussess || twoFactorResponse: " + twoFactorResponse);

				if(twoFactorResponse.getDetails().equals("OTP Matched")) {					
					/* save verified in user db */
					userEntity.setVerified(Boolean.TRUE);
					userRepo.save(userEntity);
				}else {
					throw new TwoFactorException(twoFactorResponse.getDetails());
				}
			} else {
				log.info("Not Sussess ||" + twoFactorResponse.getStatus());
				if (twoFactorResponse.getDetails().equals("Invalid APIKey + SessionId combination")) {
					/*
					 * Invalid API / SessionId Combination - No Entry Exists
					 */
					throw new GenericException("Something went wrong. Please try again after sometime");
				} else {
					/*
					 * OTP Mismatch
					 */
					throw new TwoFactorException(twoFactorResponse.getDetails());
				}
			}
		} else {
			throw new GenericException("Something went wrong. Please try again after sometime");
		}
		return twoFactorResponse;
	}

	private String getApiKey() throws GenericException {
		PartnerEntity partnerEntity = partnerRepo.findByName(PARTNER_TWO_FACTOR).orElse(null);
		if (partnerEntity != null) {
			return partnerEntity.getApiKey();
		} else {
			throw new GenericException(PARTNER_TWO_FACTOR + " partner not present.");
		}
	}
}
