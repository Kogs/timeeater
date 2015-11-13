/**
 *
 */
package de.kogs.timeeater.data.comparator;

import de.kogs.timeeater.data.JobVo;

import java.util.Comparator;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class LastWorkComparator implements Comparator<JobVo> {

	private WorkComparator workComparator = new WorkComparator();
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
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
