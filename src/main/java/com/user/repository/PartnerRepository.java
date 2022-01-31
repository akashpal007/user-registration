package com.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.entity.PartnerEntity;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, Long> {

	Optional<PartnerEntity> findByName(String partnerTwoFactor);

}
