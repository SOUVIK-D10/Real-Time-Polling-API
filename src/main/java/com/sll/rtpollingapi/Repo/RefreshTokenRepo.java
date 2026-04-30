package com.sll.rtpollingapi.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sll.rtpollingapi.Model.RefreshToken;
@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Integer>{
    RefreshToken findByToken(String token);
}
