package com.sll.rtpollingapi.Repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sll.rtpollingapi.Model.Poll;

import jakarta.transaction.Transactional;
@Repository
public interface PollRepo extends JpaRepository<Poll,Integer>{

    @Query("SELECT p FROM Poll p WHERE p.ownerId = :ownerId")
    List<Poll> myPolls(int ownerId, Pageable p);

    @Query("SELECT p FROM Poll p WHERE p.ownerId != :ownerId")
    List<Poll> notMyPolls(int ownerId, Pageable p);
    
    @Modifying
    @Transactional
    int deleteByIdAndUserId(int pollId, int userId);

    
}
