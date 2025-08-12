package com.miaoubich.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miaoubich.user.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
