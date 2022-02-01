package com.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByMobile(String phoneNumber);

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByEmailAndVerified(String email, Boolean verified);

	Optional<UserEntity> findByIdAndVerified(Long userId, Boolean true1);

	Optional<UserEntity> findByEmailOrMobile(String email, String mobileNumber);

}
