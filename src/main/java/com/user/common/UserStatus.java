package com.user.common;

public enum UserStatus {
	UNKNOWN_USER("Unknown User"),
	UNAUTHORISED_USER ("Unauthorised User"),
	VERIFICATION_PENDING ("Verification Pending"),
	VERIFIED_USER("Verified User");
	
	private final String name;

	private UserStatus(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
