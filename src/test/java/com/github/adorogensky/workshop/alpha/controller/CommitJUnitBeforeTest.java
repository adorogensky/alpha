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
		userId = user.getId();
		entityManager.flush();

		/*
			Manually commit and end current test transaction because @Commit won't work on @Before and @After methods.
			Right after that start a new test transaction so that the following test execution runs in a transaction.
		 */
		TestTransaction.flagForCommit();
		TestTransaction.end();
	}

	@Test
	public void emptyTest() {
		assertNotNull(entityManager.find(User.class, userId));
	}

	@After
	public void tearDown() {
		TestTransaction.start();

		entityManager.createQuery(
			"DELETE User user WHERE user.id = :userId"
		).setParameter(
			"userId", userId
		).executeUpdate();

		/*
			Manually commit and end current test transaction because @Commit won't work on @Before and @After methods.
			@Commit does work with @Test methods since @Before, @Test and @After code is logically grouped
			and consecutively runs in the same test transaction when either the test class or the test method is
			annotated @Transactional.
		*/

		TestTransaction.flagForCommit();
		TestTransaction.end();
	}
}
