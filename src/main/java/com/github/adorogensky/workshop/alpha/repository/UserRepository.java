package com.github.adorogensky.workshop.alpha.repository;

import com.github.adorogensky.workshop.alpha.domain.entity.User;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface UserRepository extends Repository<User, Integer> {

	List<User> findAll();

	User save(User user);

	User findByLogin(String login);

	User findById(Integer id);

	void deleteById(Integer id);

}
