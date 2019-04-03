package com.github.adorogensky.workshop.alpha.service;

import com.github.adorogensky.workshop.alpha.domain.dto.UserProfileOutputTO;
import com.github.adorogensky.workshop.alpha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<UserProfileOutputTO> getUsers() {
		List<UserProfileOutputTO> outputUserProfileList = new ArrayList<UserProfileOutputTO>();

		userRepository.findAll().forEach(
			userProfile -> {
				UserProfileOutputTO outputUserProfile = new UserProfileOutputTO();
				outputUserProfile.setLogin(userProfile.getLogin());
				outputUserProfile.setCreated(userProfile.getCreated());
				outputUserProfile.setModified(userProfile.getModified());

				outputUserProfileList.add(outputUserProfile);
			}
		);

		return outputUserProfileList;
	}
}
