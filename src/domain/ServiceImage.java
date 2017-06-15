package domain;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "qx_serviceimage")
public class ServiceImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7745909974578624514L;
	private int id;
	private String link;
	private BusinessService service;
	private File image;

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@ManyToOne(targetEntity = BusinessService.class)
	@JoinColumn(name = "service")
	public BusinessService getService() {
		return service;
	}

	public void setService(BusinessService service) {
		this.service = service;
	}

	@Transient
	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

}
