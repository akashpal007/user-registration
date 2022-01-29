package com.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
	@Column(name = "username")
	String email;
	
	@Column(name = "password")
	String password;
	
	@Column(name = "name")
	String name;
	
	@Column(name = "mobile")
	Integer mobile;
	
	@Column(name = "date_of_birth")
	Date dateOfBirth;
	
	@Column(name = "address")
	String address;
	
	@Column(name = "access_token")
	String accessToken;
	
	@Column(name = "session_id")
	String sessionId;
	
	@Column(name = "verified")
	Boolean verified;
	
}
