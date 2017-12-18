package com.jd.survey.domain.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "sec_user_password")
@NamedQueries({
	@NamedQuery(name = "UserPassword.findAll", query = "select o from UserPassword o order by o.id"),
	@NamedQuery(name = "UserPassword.findById", query = "select o from UserPassword o where o.id = ?1"),	
	@NamedQuery(name = "UserPassword.getCount", query = "select count(o) from UserPassword o")	
})
public class UserPassword {

	
	
	
	public UserPassword() {
		super();
	}

	public UserPassword(Long id, String password) {
		super();
		this.id = id;
		this.password = password;
	}

	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(length = 500, nullable= false)
	private String password;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
