package com.jd.survey.dto;




public class UserHolder {

	String password="";
	String login, email;
	Long id;
	String selected;

	public UserHolder() {
		super();
	}



	public UserHolder(String password, String login, String email, Long id) {
		super();
		this.password = password;
		this.login = login;
		this.email = email;	
		this.id = id;
	}



	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
