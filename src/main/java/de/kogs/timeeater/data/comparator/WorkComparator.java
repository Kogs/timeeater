/**
 *
 */
package de.kogs.timeeater.data.comparator;

import de.kogs.timeeater.data.LoggedWork;

import java.util.Comparator;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class WorkComparator implements Comparator<LoggedWork> {
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(LoggedWork o1, LoggedWork o2) {
		if (o1.getLogEnd() == o2.getLogEnd()) {
			return 0;
		}
		if (o1.getLogEnd() == null) {
			return -1;
		}
		if (o2.getLogEnd() == null) {
			return 1;
		}

		return Long.compare(o1.getLogEnd(), o2.getLogEnd());
	}

}
