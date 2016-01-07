/**
 *
 */
package de.kogs.timeeater.data.comparator;

import de.kogs.timeeater.data.JobVo;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class JobNameComparator implements Comparator<JobVo> {
	
	private Collator collator;
	
	public JobNameComparator () {
		collator = Collator.getInstance(Locale.GERMAN);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(JobVo o1, JobVo o2) {
		return collator.compare(o1.getName(), o2.getName());
	}

}
