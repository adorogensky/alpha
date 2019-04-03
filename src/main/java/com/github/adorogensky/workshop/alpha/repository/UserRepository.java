package com.github.adorogensky.workshop.alpha.repository;

import com.github.adorogensky.workshop.alpha.domain.entity.UserProfile;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserProfile, Integer> { }
