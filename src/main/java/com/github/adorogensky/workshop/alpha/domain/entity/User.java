package com.github.adorogensky.workshop.alpha.domain.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(schema = "alpha", name = "user_profile")
public class User extends MyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_sequence_generator")
	@SequenceGenerator(name = "user_id_sequence_generator", schema = "alpha", allocationSize = 1, sequenceName = "user_id_seq")
	private Integer id;

	private String login;

	private String password;

	@CreationTimestamp
	private LocalDateTime created;

	@UpdateTimestamp
	private LocalDateTime modified;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) return true;
		if (that == null || getClass() != that.getClass()) return false;
		User user = (User) that;
		return Objects.equals(login, user.login) &&
			Objects.equals(password, user.password) &&
			Objects.equals(created, user.created) &&
			Objects.equals(modified, user.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(login, password, created, modified);
	}

	@Override
	public String toString() {
		return toString(this);
	}
}
