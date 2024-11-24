package com.dppl.mycards.card.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.repository.model.UserActivity;

@Repository
public interface PartnerActivityRepository extends JpaRepository<PartnerActivity, Long> {

	@Query("SELECT pa FROM PartnerActivity pa "
	        + "WHERE (:startDate IS NULL OR pa.eventTimestamp >= :startDate) "
	        + "AND (:endDate IS NULL OR pa.eventTimestamp <= :endDate) "
	        + "AND (:eventType IS NULL OR pa.eventType = :eventType) "
	        + "AND pa.partnerId = :partnerId")
	List<PartnerActivity> findByCriteria(
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate, 
	        @Param("eventType") String eventType,
	        @Param("partnerId") Long partnerId);

	@Query("SELECT pa FROM PartnerActivity pa "
			+ "WHERE (:eventType IS NULL OR pa.eventType = :eventType)")
	List<PartnerActivity> findByEventType(@Param("eventType") String string);
}
