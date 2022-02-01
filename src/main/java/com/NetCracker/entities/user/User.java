package com.NetCracker.entities.user;

import com.NetCracker.entities.patient.Patient;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Getter
@Setter
@ToString
@Entity
@Table(	name = "users",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "username"),
				@UniqueConstraint(columnNames = "email")
		})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	@JsonBackReference
//	@JsonIgnore
	private Patient patient;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "patronymic")
	private String patronymic;

	@Column(name = "phone")
	private String phone;

	@NotBlank
	@Size(max = 20)
	private String userName;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	private boolean isEnabled;
	@NotBlank
	@Size(max = 120)
	private String password;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User() {
	}

	public User(String firstName, String lastName, String patronymic, String phone,String userName, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.patronymic = patronymic;
		this.phone = phone;
		this.userName = userName;
		this.email = email;
		this.password = password;
	}

	public User(String firstName, String lastName, String patronymic, String phone,String username, String email, String password, Set<Role> roles) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.patronymic = patronymic;
		this.phone = phone;
		this.userName = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

public User(User user)
	{
		this(user.firstName, user.lastName, user.patronymic, user.phone, user.userName, user.email, user.password, user.roles);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return userName;
	}

	public void setUsername(String username) {
		this.userName = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
