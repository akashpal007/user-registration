package com.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.entity.PartnerEntity;

public interface PartnerRepository extends JpaRepository<PartnerEntity, Long> {

	Optional<PartnerEntity> finaByName(String partnerTwoFactor);

}
