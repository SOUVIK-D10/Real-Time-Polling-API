package com.sll.rtpollingapi.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sll.rtpollingapi.Model.Option;

import jakarta.transaction.Transactional;
@Repository
public interface OptionRepo extends JpaRepository<Option,Integer> {

    List<Option> findByPollId(int id);

    @Modifying
    @Transactional
    @Query("UPDATE Option o SET o.vote = o.vote + 1 WHERE o.pollId = :pollId AND o.id = :optionId")
    void vote(int pollId, int optionId);

    @Modifying
    @Transactional
    @Query("UPDATE Option o SET o.vote = o.vote - 1 WHERE o.pollId = :pollId AND o.id = :optionId")
    void cancel(int optionId, int pollId);

    @Modifying
    @Transactional
    void deleteAllByPollId();
    
}
