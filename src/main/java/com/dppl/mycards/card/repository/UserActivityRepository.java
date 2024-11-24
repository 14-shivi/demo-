package com.dppl.mycards.card.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dppl.mycards.card.repository.model.UserActivity;

//UserActivityRepository.java
@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

	@Query("SELECT ua FROM UserActivity ua WHERE ua.eventType LIKE %:eventType%")
	public List<UserActivity> findByEventType(@Param("eventType") String eventType);
	
	@Query("SELECT ua FROM UserActivity ua JOIN User u ON ua.userId=u.userId WHERE mobile=:mobile")
	public List<UserActivity> findByMobile(@Param("mobile") String mobile);
	
	@Query("SELECT ua FROM UserActivity ua JOIN User u ON ua.userId=u.userId WHERE email=:email")
	public List<UserActivity> findByEmail(@Param("email") String email);

	public List<UserActivity> findByUserId(Long userId);

	@Query("SELECT ua FROM UserActivity ua JOIN User u ON ua.userId = u.id "
	        + "WHERE (:startDate IS NULL OR ua.eventTimestamp >= :startDate) "
	        + "AND (:endDate IS NULL OR ua.eventTimestamp <= :endDate) "
	        + "AND (:eventType IS NULL OR ua.eventType = :eventType) "
	        + "AND (:email IS NULL OR u.email = :email) "
	        + "AND (:mobile IS NULL OR u.mobile = :mobile)")
	List<UserActivity> findByCriteria(
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate, 
	        @Param("eventType") String eventType, 
	        @Param("email") String email, 
	        @Param("mobile") String mobile);


	List<UserActivity> findAll();
}
