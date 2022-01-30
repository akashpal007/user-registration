package com.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column(name = "username", unique = true)
	String email;

	@Column(name = "password")
	String password;

	@Column(name = "name")
	String name;

	@Column(name = "mobile", unique = true)
	String mobile;

	@Column(name = "date_of_birth")
	Date dateOfBirth;

	@Column(name = "address")
	String address;

	@Column(name = "access_token")
	String accessToken;

	@Column(name = "session_id")
	String sessionId;

	/*
	 *  true	= The user is registered with the system but OTP authentication is pending.
	 *  false	= The user is registered with the system and OTP authentication is done.
	 */
	@Column(name = "verified", columnDefinition="BOOLEAN DEFAULT false")
	Boolean verified;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_timestamp", updatable = false)
	@CreationTimestamp
	Date createdTimestamp;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_timestamp")
	@UpdateTimestamp
	Date updatedTimestamp;

}
