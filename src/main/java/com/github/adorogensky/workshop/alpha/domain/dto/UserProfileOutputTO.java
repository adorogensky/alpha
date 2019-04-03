package com.github.adorogensky.workshop.alpha.domain.dto;

import java.time.LocalDateTime;

public class UserProfileOutputTO {

	private String login;

	private LocalDateTime created;

	private LocalDateTime modified;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}
}
