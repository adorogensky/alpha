package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.entity.User;
import com.github.adorogensky.workshop.alpha.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;

/*
 * This test demonstrates how to add a new user record in database.
 * It runs and commits two transactions in @Before and @After methods
 * that add a user and delete it respectively.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommitTransactionBeforeTest2 {

	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private Integer userId;

	private Integer userCount;

	@Before
	public void setUp() {
		userCount = userRepository.findAll().size();
		User addUser = new User();
		addUser.setLogin("test2");
		addUser.setPassword("test2");
		userRepository.save(addUser);
		userId = addUser.getId();
	}

	@Test
	public void findNewlyAddedUser() {
		List userCount = entityManager.createNativeQuery("SELECT COUNT(id) FROM alpha.user_profile").getResultList();
		assertEquals(1, userCount.size());
		assertEquals(BigInteger.valueOf(this.userCount + 1), userCount.get(0));
	}

	@After
	public void tearDown() {
		userRepository.deleteById(userId);
	}
}
