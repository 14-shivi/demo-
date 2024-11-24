package com.dppl.mycards.card.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dppl.mycards.card.repository.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByMobile(String mobile);

	Optional<User> findByEmail(String email);

	boolean existsByMobile(String mobile);

}
