package ch.unibe.ese.Tutorfinder.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
/**
 * Entity for user, holding following values:<br>
 * {@code id} is the id of the subject and is generated automatically<br>
 * {@code firstName} the users first name<br>
 * {@code lastName} the users last name<br>
 * {@code email} is used for referencing between user and subject (same for id)<br>
 * {@code password} is need to identify the right user to the right email<br>
 * {@code role} to difference between students and tutors
 * 
 * @version	2.0
 *
 */
@Entity
@Table(name="user", uniqueConstraints=@UniqueConstraint(columnNames = { "email" }))
public class User {
	
	@Id
	@GeneratedValue
	private long id;
	
	@NotNull
	private String firstName;
	
	@NotNull
	private String lastName;
	
	@NotNull
	@Column(name="email")
	private String email;
	
	@NotNull
	private String password;
	
	@NotNull
	private String role;
	
	@NotNull
	@OneToOne(cascade = {CascadeType.ALL})
	private Profile profile;
	
	/* Constructor */
	public User() {
		super();
	}
	
	/* Getters and Setters */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		assert firstName != null : "Users firstName is not allowed to be null!";
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		assert lastName != null : "Users lastName is not allowed to be null!";
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		assert email != null : "Users email is not allowed to be null!";
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		assert password != null : "Users password is not allowed to be null!";
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		assert role != null : "Users role is not allowed to be null!";
		this.role = role;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		assert profile != null : "Users profile is not allowed to be null!";
		this.profile = profile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", role="
				+ role + "\n" + profile + "\n";
	}
	
}