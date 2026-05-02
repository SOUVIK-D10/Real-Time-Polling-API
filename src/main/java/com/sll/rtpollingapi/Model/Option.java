package com.sll.rtpollingapi.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int pollId;
    private String data;
    private int vote;
    public Option(){}
	public Option(int pollId, String data) {
		this.pollId = pollId;
		this.data = data;
		this.vote = 0;
	}
    public String getData() {
        return data;
    }
    public int getId() {
        return id;
    }
    public int getPollId() {
        return pollId;
    }
    public int getVote() {
        return vote;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setPollId(int pollId) {
        this.pollId = pollId;
    }
}
