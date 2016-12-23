package domain;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "study")
public class Study {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String ownerEmail;
	private String incentive;
	private boolean hasPay;
	
	
	public String getName() {
		return name;
	}
	@XmlElement
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	@XmlElement
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	@XmlElement
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public String getIncentive() {
		return incentive;
	}
	@XmlElement
	public void setIncentive(String incentive) {
		this.incentive = incentive;
	}
	public boolean isPaid() {
		return hasPay;
	}
	@XmlElement
	public void setPaid(boolean hasPay) {
		this.hasPay = hasPay;
	}
	
	
}
