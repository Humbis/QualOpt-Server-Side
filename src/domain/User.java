package domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String email;
	private String password;
	private String profession;
	private String institution;
	private String description;

	public User() {
	}

	public User(String name, String password, String profession, String institution, String description) {
		this.email = name;
		this.password = password;
		this.profession = profession;
		this.institution = institution;
		this.description = description;
	}


	public String getEmail() {
		return email;
	}

	@XmlElement
	public void setEmail(String name) {
		this.email = name;
	}

	public String getProfession() {
		return profession;
	}

	@XmlElement
	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getInstitution() {
		return institution;
	}

	@XmlElement
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getPassword() {
		return password;
	}

	@XmlElement
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	@XmlElement
	public void setDescription(String description) {
		this.description = description;
	}
}
