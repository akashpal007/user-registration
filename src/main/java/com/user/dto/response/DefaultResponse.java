package com.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.user.common.Status;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultResponse {
	Status status;
	String details;
}
