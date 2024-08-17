/**
 *
 */
package de.kogs.timeeater.data;

import java.util.Date;

/**
 * 
 *
 */
public class LoggedWork {

	private Date logDate;
	
	private Long logStart;
	private Long logEnd;
	

	public Date getLogDate() {
		return logDate;
	}
	
	public Long getLogStart() {
		return logStart;
	}
	
	public Long getLogEnd() {
		if(logEnd == null){
			return System.currentTimeMillis();
		}
		return logEnd;
	}
	
	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
	
	public void setLogStart(Long logStart) {
		this.logStart = logStart;
	}
	
	public void setLogEnd(Long logEnd) {
		this.logEnd = logEnd;
	}
	
}
