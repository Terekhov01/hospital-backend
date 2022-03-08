package com.NetCracker.entities.user;

import com.NetCracker.entities.patient.Patient;
import com.NetCracker.entities.user.role.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
//	@JsonBackReference
//	@JsonIgnore
	@JsonBackReference
	private Patient patient;


	@NotBlank
	@Column(name = "first_name")
	private String firstName;

	@NotBlank
	@Column(name = "last_name")
	private String lastName;

	@Column(name = "patronymic")
	private String patronymic;

	@Column(name = "phone")
	// https://stackoverflow.com/a/18626090/12287688 - regex phone number validation
	@Pattern(regexp = "\\(?\\+[0-9]{1,3}\\)? ?-?[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})? ?(\\w{1,10}\\s?\\d{1,6})?")
	private String phone;

	@NotBlank
	@Size(min = 3, max = 20)
	private String userName;

	@NotBlank
	@Size(max = 50)
	@Pattern(regexp = ".*@.*")
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 40)
	private String password;

	@Column(columnDefinition = "boolean default true")
	private boolean isEnabled;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User() {
	}

	public User(String firstName, String lastName, String patronymic, String phone, String userName, String email, String password) {
		this.patient = null;
		this.firstName = firstName;
		this.lastName = lastName;
		this.patronymic = patronymic;
		this.phone = phone;
		this.userName = userName;
		this.email = email;
		this.password = password;
	}

	public User(String firstName, String lastName, String patronymic, String phone,String username, String email, String password, Set<Role> roles) {
		this.patient = null;
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

	public String getFullNameFormatted()
	{
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(this.lastName).append(' ');
		nameBuilder.append(this.firstName).append(' ');
		nameBuilder.append(this.patronymic);
		return nameBuilder.toString();
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
