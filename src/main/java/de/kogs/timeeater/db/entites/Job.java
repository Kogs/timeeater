/**
 *
 */
package de.kogs.timeeater.db.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */

@Entity
@Table(name = "jobs")
public class Job {
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "desciption")
	private String description;
//
//	@JoinColumn(name = "userId")
//	private User user;
	
	@Column(name = "userid")
	private Long userID;
	
	@Column(name = "activeworkdid")
	private Long activeWorkID;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public Long getActiveWorkID() {
		return activeWorkID;
	}

	public void setActiveWorkID(Long activeWorkID) {
		this.activeWorkID = activeWorkID;
	}
	
}
