package de.kogs.timeeater.data.comparator;

import de.kogs.timeeater.data.JobVo;

import java.util.Comparator;

public class LastWorkComparator implements Comparator<JobVo> {

	private WorkComparator workComparator = new WorkComparator();
	
	@Override
	public int compare(JobVo o1, JobVo o2) {
		if (o1 == null || o1.getLastWork() == null) {
			return 1;
		}
		if (o2 == null || o2.getLastWork() == null) {
			return -1;
		}
		
		return -workComparator.compare(o1.getLastWork(), o2.getLastWork());
	}
	
}
