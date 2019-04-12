package com.github.adorogensky.workshop.alpha.repository;

import com.github.adorogensky.workshop.alpha.domain.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

	User findByLogin(String login);
}
