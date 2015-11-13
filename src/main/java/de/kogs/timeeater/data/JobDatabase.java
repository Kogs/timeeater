/**
 *
 */
package de.kogs.timeeater.data;

import org.springframework.context.support.AbstractApplicationContext;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class JobDatabase extends JobProvider {

	private AbstractApplicationContext context;
	
	public JobDatabase (AbstractApplicationContext context) {
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#getActiveJob()
	 */
	@Override
	public JobVo getActiveJob() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#startWorkOnJob(java.lang.String)
	 */
	@Override
	public void startWorkOnJob(String jobName) {
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#startWorkOnJob(de.kogs.timeeater.data.JobVo)
	 */
	@Override
	public void startWorkOnJob(JobVo job) {
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#stopWork()
	 */
	@Override
	public void stopWork() {
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#getKownJobs()
	 */
	@Override
	public Collection<JobVo> getKownJobs() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#getJobsForDay(java.util.Date)
	 */
	@Override
	public Collection<JobVo> getJobsForDay(Date day) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#getTimeForDay(java.util.Date)
	 */
	@Override
	public long getTimeForDay(Date day) {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#getJobsForRange(java.util.Date, java.util.Date)
	 */
	@Override
	public List<JobVo> getJobsForRange(Date start, Date end) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#getJob(java.lang.String)
	 */
	@Override
	public JobVo getJob(String name) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#removeJob(de.kogs.timeeater.data.JobVo)
	 */
	@Override
	public void removeJob(JobVo j) {
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#save()
	 */
	@Override
	public void save() {
	}
	
}
