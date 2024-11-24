package com.dppl.mycards.card.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dppl.mycards.card.repository.model.OtpDetail;

@Repository
public interface OtpDetailRepository extends JpaRepository<OtpDetail, Long> {

	Optional<OtpDetail> findByUserId(Long userId);

	Optional<OtpDetail> findByEmail(String email);

	Optional<OtpDetail> findByMobile(String mobile);

	Optional<OtpDetail> findByPartnerId(Long id);

}
