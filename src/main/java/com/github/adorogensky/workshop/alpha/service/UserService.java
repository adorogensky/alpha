package com.github.adorogensky.workshop.alpha.service;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserProfileInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserProfileOutputTO;
import com.github.adorogensky.workshop.alpha.domain.entity.UserProfile;
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

	public List<UserProfileOutputTO> getUsers() {
		List<UserProfileOutputTO> outputUserProfileList = new ArrayList<UserProfileOutputTO>();

		userRepository.findAll().forEach(
			userProfile -> {
				UserProfileOutputTO outputUserProfile = new UserProfileOutputTO();
				outputUserProfile.setId(userProfile.getId());
				outputUserProfile.setLogin(userProfile.getLogin());
				outputUserProfile.setCreated(userProfile.getCreated());
				outputUserProfile.setModified(userProfile.getModified());

				outputUserProfileList.add(outputUserProfile);
			}
		);

		return outputUserProfileList;
	}

	@Transactional
	public UserProfileOutputTO addUser(AddUserProfileInputTO addUserInput) {
		UserProfile newUser = new UserProfile();
		newUser.setLogin(addUserInput.getLogin());
		newUser.setPassword(addUserInput.getPassword());

		userRepository.save(newUser);
		entityManager.flush();

		UserProfileOutputTO addUserOutput = new UserProfileOutputTO();
		addUserOutput.setId(newUser.getId());
		addUserOutput.setLogin(addUserInput.getLogin());
		addUserOutput.setCreated(newUser.getCreated());

		return addUserOutput;
	}
}
