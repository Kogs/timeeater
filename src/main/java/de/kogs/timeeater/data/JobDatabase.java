/**
 *
 */
package de.kogs.timeeater.data;

import org.springframework.context.support.AbstractApplicationContext;

import de.kogs.timeeater.db.SessionHandler;
import de.kogs.timeeater.db.dao.JobDAO;
import de.kogs.timeeater.db.dao.WorkDAO;
import de.kogs.timeeater.db.entites.Job;
import de.kogs.timeeater.db.entites.Work;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class JobDatabase extends JobProvider {

	private AbstractApplicationContext context;
	
	private JobDAO jobDAO;
	
	private WorkDAO workDAO;
	
	private Job activeJob;

	public JobDatabase (AbstractApplicationContext context) {
		this.context = context;
		jobDAO = context.getBean(JobDAO.class);
		workDAO = context.getBean(WorkDAO.class);
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#getActiveJob()
	 */
	@Override
	public JobVo getActiveJob() {
		return toVO(activeJob);
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.JobProvider#startWorkOnJob(java.lang.String)
	 */
	@Override
	public void startWorkOnJob(String jobName) {
		Job job = jobDAO.findOneByUserAndName(SessionHandler.logedInUser.getId(), jobName);
		if (job == null) {
			job = createJob(jobName);
		}
		startWorkOnJob(job);
	}
	
	private Job createJob(String jobName) {
		Job job = new Job();
		job.setName(jobName);
		job.setUserID(SessionHandler.logedInUser.getId());
		job = jobDAO.save(job);
		return job;
	}
	
	private Work createWork(Job job) {
		Work work = new Work();
		work.setJobId(job.getId());
		work.setStart(System.currentTimeMillis());
		work = workDAO.save(work);
		return work;
	}
	
	public void startWorkOnJob(Job job) {
		if (activeJob == null || activeJob.getActiveWorkID() == null) {
			activeJob = job;
			
			Work work = createWork(job);
//			
//			activeJob.setActiveWork(work);
//			activeJob.getWorks().add(work);
			
			activeJobEvent();
		}
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
		return toVO(jobDAO.findAllByUser(SessionHandler.logedInUser.getId()));
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
	
	private Job fromVO(JobVo jobVo) {
		return null;
	}
	
	private Collection<JobVo> toVO(Collection<Job> jobs) {
		List<JobVo> vos = new ArrayList<>(jobs.size());
		for (Job job : jobs) {
			vos.add(toVO(job));
		}
		return vos;
	}
	
	private JobVo toVO(Job job) {
		if (job == null) {
			return null;
		}
		JobVo vo = new JobVo();
		vo.name = job.getName();
		vo.setDescription(job.getDescription());
		
		if (job.getActiveWorkID() != null) {
			vo.setActiveWork(toVO(workDAO.findOne(job.getActiveWorkID())));
		}
		
//		vo.getWorks().addAll(workDAO.)
		
		return vo;
	}
	
	private LoggedWork toVO(Work work) {
		
		LoggedWork vo = new LoggedWork();
		vo.setLogEnd(work.getEnd());
		vo.setLogStart(work.getStart());
		vo.setLogDate(new Date(work.getStart()));
		return vo;
	}
	

}
