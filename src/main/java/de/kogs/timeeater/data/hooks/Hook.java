/**
 *
 */
package de.kogs.timeeater.data.hooks;

import de.kogs.timeeater.data.Job;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public abstract class Hook {
	
	protected Job job;
	
	private Map<String, String> properties = new HashMap<>();

	public Hook () {
	}
	
	public Hook (Job job) {
		this.job = job;
	}
	
	public abstract void action();
	
	public Job getJob() {
		return job;
	}
	
	public void setJob(Job job) {
		this.job = job;
	}

	public Map<String, String> getProperties() {
		return properties;
	}



	
}
