package com.dppl.mycards.card.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dppl.mycards.card.repository.model.Partner;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

	@Query("SELECT p FROM Partner p WHERE "
			+ "(:email IS NULL OR p.email = :email) AND (:mobile IS NULL OR p.mobile = :mobile)")
	Optional<Partner> findByEmailOrMobile(@Param("email") String email, @Param("mobile") String mobile);

	Optional<Partner> findByMobile(String mobile);

	Optional<Partner> findByEmail(String email);

}
