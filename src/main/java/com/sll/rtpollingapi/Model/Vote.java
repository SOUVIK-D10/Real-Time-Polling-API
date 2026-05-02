package com.sll.rtpollingapi.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int voterId;
    private int pollId;
    private int optionId;
    public Vote(){}
	public Vote(int voterId, int pollId, int optionId) {
		this.voterId = voterId;
		this.pollId = pollId;
		this.optionId = optionId;
	}
    public int getId() {
        return id;
    }
    public int getOptionId() {
        return optionId;
    }
    public int getPollId() {
        return pollId;
    }
    public int getVoterId() {
        return voterId;
    }
    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }
    public void setPollId(int pollId) {
        this.pollId = pollId;
    }
    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }
}
