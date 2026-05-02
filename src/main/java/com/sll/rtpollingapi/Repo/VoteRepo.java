package com.sll.rtpollingapi.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.sll.rtpollingapi.Model.Vote;

import jakarta.transaction.Transactional;
@Repository
public interface VoteRepo extends JpaRepository<Vote,Integer> {

    Vote findByVoterIdAndPollId(int voterId, int pollId);

    @Modifying
    @Transactional
    void deleteAllByPollId();

    
}
