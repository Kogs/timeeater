/**
 *
 */
package de.kogs.timeeater.data.hooks;

import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class DefaultHook {
	
	private String hookClass;
	private Map<String, String> properties = new HashMap<>();
	private String regex;
	
	public DefaultHook () {
	}
	
	public void applyIfMatch(Job job) {
		Pattern p = Pattern.compile(regex);
		
		Matcher matcher = p.matcher(job.getName());
		if (matcher.matches()) {
			System.out.println("Pattern Matches");
			try {
				Class<? extends Hook> clazz = (Class<? extends Hook>) Class.forName(hookClass);
				HookManager hookManager = JobManager.hookInstance();
				if (hookManager.getHookForJob(job, clazz) == null) {
					Hook h = clazz.newInstance();
					h.setJob(job);
					h.getProperties().putAll(properties);
					
					hookManager.addHook(h);
					System.out.println("Hook added for: " + job + " " + hookClass);
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String getHookClass() {
		return hookClass;
	}
	
	public void setHookClass(String hookClass) {
		this.hookClass = hookClass;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public String getRegex() {
		return regex;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
