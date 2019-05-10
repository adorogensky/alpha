package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;

/*
 * This test demonstrates how to add a new user record in database.
 * It runs and commits two transactions in @Before and @After methods
 * that add a user and delete it respectively.
 */
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class CommitTransactionBeforeTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private int userId;

	private int userCount;

	@Before
	public void setUp() {
		userCount = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM alpha.user_profile", Integer.class);

		User user = new User();
		user.setLogin("test");
		user.setPassword("test");

		entityManager.persist(user);
		userId = user.getId();

		/*
			Manually commit and end current test transaction because @Commit won't work on @Before and @After methods.
			Right after that start a new test transaction so that the following test execution runs in a transaction.
		 */
		TestTransaction.flagForCommit();
		TestTransaction.end();
	}

	@Test
	public void findNewlyAddedUser() {
		assertEquals(Integer.valueOf(userCount + 1), jdbcTemplate.queryForObject("SELECT COUNT(id) FROM alpha.user_profile", Integer.class));
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
