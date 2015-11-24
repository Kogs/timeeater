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
 *
 */
@Table(name = "work")
@Entity
public class Work {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "start")
	private Long start;
	
	@Column(name = "end")
	private Long end;
	
	@Column(name = "jobid")
	private Long jobId;
	
	public Long getId() {
		return id;
	}
	
	public Long getStart() {
		return start;
	}
	
	public Long getEnd() {
		return end;
	}
	
	public Long getJobId() {
		return jobId;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setStart(Long start) {
		this.start = start;
	}
	
	public void setEnd(Long end) {
		this.end = end;
	}
	
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	
}
