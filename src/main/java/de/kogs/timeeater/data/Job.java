/**
 *
 */
package de.kogs.timeeater.data;

import de.kogs.timeeater.data.comparator.WorkComparator;
import de.kogs.timeeater.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class Job {

	private String name;

	private String description;
	
	private List<LoggedWork> works = new ArrayList<LoggedWork>();

	private LoggedWork activeWork;

	/**
	 * 
	 */
	public Job() {

	}

	public LoggedWork getLastWork() {
		works.sort(new WorkComparator());
		return works.get(works.size() - 1);
	}
	
	public long getFullWorkTime() {
		return countWorkTime(works);
	}

	public long getWorkTime(Date date) {
		return countWorkTime(getWorkForDay(date));
	}

	public List<LoggedWork> getWorkForDay(Date date) {
		List<LoggedWork> work = new ArrayList<>();
		for (LoggedWork aWork : works) {
			if (Utils.isSameDay(aWork.getLogDate(), date)) {
				work.add(aWork);
			}
		}
		return work;
	}

	private static long countWorkTime(List<LoggedWork> work) {
		long time = 0;
		for (LoggedWork aWork : work) {
			long end;
			if (aWork.getLogEnd() == null) {
				end = System.currentTimeMillis();
			} else {
				end = aWork.getLogEnd();
			}
			time += end - aWork.getLogStart();
		}
		return time;
	}

	public LoggedWork getNextWork(LoggedWork work) {
		List<LoggedWork> workForDay = getWorkForDay(work.getLogDate());

		LoggedWork nextWork = null;

		for (LoggedWork loggedWork : workForDay) {
			if (loggedWork.getLogStart() > work.getLogEnd()) {
				if (nextWork == null
						|| (loggedWork.getLogStart() - work.getLogEnd()) < (nextWork
								.getLogStart() - work.getLogEnd())) {
					nextWork = loggedWork;
				}
			}
		}
		return nextWork;
	}


	public List<LoggedWork> getWorkInRange(Date startDate , Date endDate){
		List<LoggedWork> work = new ArrayList<>();
		for (LoggedWork aWork : works) {
			if (Utils.isInRange(aWork.getLogDate(), startDate,endDate)) {
				work.add(aWork);
			}
		}
		return work;
	}
	public Long getWorkTimeInRange(Date startDate,Date endDate){
		return countWorkTime(getWorkInRange(startDate, endDate));
	}
	public LoggedWork getPreviousWork(LoggedWork work) {
		List<LoggedWork> workForDay = getWorkForDay(work.getLogDate());

		LoggedWork prevWork = null;

		for (LoggedWork loggedWork : workForDay) {
			if (loggedWork.getLogEnd() < work.getLogStart()) {
				if (prevWork == null
						|| (loggedWork.getLogEnd() - work.getLogStart()) > (prevWork
								.getLogEnd() - work.getLogStart())) {
					prevWork = loggedWork;
				}
			}
		}
		return prevWork;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Job other = (Job) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

}
