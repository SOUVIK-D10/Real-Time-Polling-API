package com.sll.rtpollingapi.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sll.rtpollingapi.Model.Users;
@Repository
public interface UserRepo extends JpaRepository<Users,Integer> {
    Users findByEmail(String email);
}
