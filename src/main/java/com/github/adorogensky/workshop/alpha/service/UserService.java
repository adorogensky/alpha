package com.github.adorogensky.workshop.alpha.service;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.ErrorTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserOutputTO;
import com.github.adorogensky.workshop.alpha.domain.entity.User;
import com.github.adorogensky.workshop.alpha.exception.BadRequestException;
import com.github.adorogensky.workshop.alpha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public List<UserOutputTO> getUsers() {
		List<UserOutputTO> outputUserProfileList = new ArrayList<UserOutputTO>();

		userRepository.findAll().forEach(
			userProfile -> {
				UserOutputTO outputUserProfile = new UserOutputTO();
				outputUserProfile.setId(userProfile.getId());
				outputUserProfile.setLogin(userProfile.getLogin());
				outputUserProfile.setCreated(userProfile.getCreated());
				outputUserProfile.setModified(userProfile.getModified());

				outputUserProfileList.add(outputUserProfile);
			}
		);

		return outputUserProfileList;
	}

	public UserOutputTO addUser(AddUserInputTO addUserInput) {
		if (addUserInput.getLogin() == null || "".equals(addUserInput.getLogin().trim())) {
			throw new BadRequestException(
				new ErrorTO(
					"http://github.com/adorogensky/workshop/alpha/user/login/empty",
					"User login cannot be empty"
				)
			);
		}

		if (addUserInput.getPassword() == null || "".equals(addUserInput.getPassword().trim())) {
			throw new BadRequestException(
				new ErrorTO(
					"http://github.com/adorogensky/workshop/alpha/user/password/empty",
					"User password cannot be empty"
				)
			);
		}

		if (userRepository.findByLogin(addUserInput.getLogin()) != null) {
			throw new BadRequestException(
				new ErrorTO(
					"http://github.com/adorogensky/workshop/alpha/user/login/taken",
					"User login '" + addUserInput.getLogin() + "' cannot be used because it's taken"
				)
			);
		}

		User newUser = new User();
		newUser.setLogin(addUserInput.getLogin());
		newUser.setPassword(addUserInput.getPassword());

		userRepository.save(newUser);
		entityManager.flush();

		UserOutputTO addUserOutput = new UserOutputTO();
		addUserOutput.setId(newUser.getId());
		addUserOutput.setLogin(addUserInput.getLogin());
		addUserOutput.setCreated(newUser.getCreated());

		return addUserOutput;
	}

	public void deleteUser(Integer id) {
		if (userRepository.findById(id) == null) {
			throw new BadRequestException(
				new ErrorTO(
					"http://github.com/adorogensky/workshop/alpha/user/id/not-found",
					"Cannot delete user with id = '" + id + "'"
				)
			);
		}

		userRepository.deleteById(id);
	}
}
