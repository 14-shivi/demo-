package com.dppl.mycards.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dppl.mycards.card.repository.model.UserCard;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, String> {

}
