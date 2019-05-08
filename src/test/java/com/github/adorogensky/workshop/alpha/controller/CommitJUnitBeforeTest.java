package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static junit.framework.TestCase.assertNotNull;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class CommitJUnitBeforeTest {

	@PersistenceContext
	private EntityManager entityManager;

	private Integer userId;

	@Before
	public void setUp() {
		User user = new User();
		user.setLogin("test");
		user.setPassword("test");
		entityManager.persist(user);
		entityManager.flush();
		TestTransaction.flagForCommit();
		TestTransaction.end();
		userId = user.getId();
		TestTransaction.start();
	}

	@Test
	public void emptyTest() {
		assertNotNull(entityManager.find(User.class, userId));
	}

	@After
	public void tearDown() {
		entityManager.createQuery("DELETE User user WHERE user.id = :userId").setParameter("userId", userId).executeUpdate();
		TestTransaction.flagForCommit();
		TestTransaction.end();
	}
}
