package com.sll.rtpollingapi.Model;

import java.time.LocalDateTime;

import com.sll.rtpollingapi.Standards.PollPolicy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int ownerId;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private String topic;
    private int policy;
    public Poll(){}
    public Poll(int ownerId, LocalDateTime expiryDate, String topic, Integer policy) {
        this.ownerId = ownerId;
        this.createdAt = LocalDateTime.now();
        this.expiryDate = expiryDate==null?createdAt.plusDays(1):expiryDate;
        this.topic = topic;
        this.policy = policy==null?PollPolicy.NO_RESTRICTION:policy;
    }
    public int getId() {
        return id;
    }
    public int getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public int getPolicy() {
        return policy;
    }
    public void setPolicy(int policy) {
        this.policy = policy;
    }
}
