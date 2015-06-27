/**
 *
 */
package de.kogs.timeeater.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.kogs.timeeater.util.Utils;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class Job {

	private String name;
	
	private List<LoggedWork> works = new ArrayList<LoggedWork>();
	
	private LoggedWork activeWork;
	
	/**
	 * 
	 */
	public Job () {
	
	}
	
	
	public long getFullWorkTime(){
		long time = 0;
		for(LoggedWork work : works){
			long end;
			if(work.getLogEnd() == null){
				end = System.currentTimeMillis();
			}else{
				end = work.getLogEnd();
			}
			time += end - work.getLogStart();
		}
		return time;
	}
	
	public long getWorkTime(Date date) {
		long time = 0;
		for(LoggedWork work : works){
			
			if(Utils.isSameDay(work.getLogDate(), date)){
				long end;				
				if(work.getLogEnd() == null){
					end = System.currentTimeMillis();
				}else{
					end = work.getLogEnd();
				}
				time += end - work.getLogStart();
			}
		}
		return time;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<LoggedWork> getWorks() {
		return works;
	}
	
	public void setWorks(List<LoggedWork> works) {
		this.works = works;
	}

	public LoggedWork getActiveWork() {
		return activeWork;
	}

	public void setActiveWork(LoggedWork activeWork) {
		this.activeWork = activeWork;
	}
	
	@Override
	public String toString() {
		return getName();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Job other = (Job) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



	
	
	
}
