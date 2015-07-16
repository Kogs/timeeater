/**
 *
 */
package de.kogs.timeeater.data.hooks;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class HookManager {
	
	private static HookManager instance;
	private JobManager jobManager;
	
	public static HookManager instance(JobManager jobManager) {
		if (instance == null) {
			instance = new HookManager(jobManager);
		}
		return instance;
	}
	
	

	private List<Hook> hooks = new ArrayList<>();
	private List<DefaultHook> defaults = new ArrayList<>();
	
	public static void main(String[] args) {
//		HookManager manager = instance();
//		DefaultHook defHook = new DefaultHook();
//		defHook.setHookClass(QuickLinkHook.class.getName());
//		defHook.setRegex("^(SPBL|FDAQA)*");
//		defHook.getProperties().put("link", "https://issues.proemion.com/browse/");
//		manager.defaults.add(defHook);
//		manager.save();
	}
	
	public HookManager (JobManager jobManager) {
		this.jobManager = jobManager;
		load();
	}
	
	public List<Hook> getHooksForJob(Job job) {
		List<Hook> hooksForJob = new ArrayList<>();
		for (Hook h : hooks) {
			if (h.getJob().equals(job)) {
				hooksForJob.add(h);
			}
		}
		return hooksForJob;
	}
	
	public <X extends Hook> X getHookForJob(Job job, Class<X> clazz) {
		for (Hook h : hooks) {
			if (h.getJob().equals(job)) {
				if (clazz.isInstance(h)) {
					return (X) h;
				}
			}
		}
		return null;
	}
	
	public <X extends Hook> List<X> getHooks(Class<X> clazz) {
		List<X> hooksForClass = new ArrayList<>();
		for (Hook h : hooks) {
			if (clazz.isInstance(h)) {
				hooksForClass.add((X) h);
			}
		}
		return hooksForClass;
	}
	
	public void save() {
		try (FileWriter writer = new FileWriter(getSaveFile())) {
			JSONArray hooksJson = new JSONArray();
			for (Hook hook : hooks) {
				JSONObject hookJson = new JSONObject();
				hookJson.put("class", hook.getClass().getName());
				hookJson.put("job", hook.getJob().getName());
				
				hookJson.put("properties", hook.getProperties());
				
				hooksJson.add(hookJson);
			}
			
			JSONArray defaultsJson = new JSONArray();
			for (DefaultHook defaultHook : defaults) {
				JSONObject defaultJson = new JSONObject();
				defaultJson.put("class", defaultHook.getHookClass());
				defaultJson.put("properties", defaultHook.getProperties());
				defaultJson.put("regex", defaultHook.getRegex());
				
				defaultsJson.add(defaultJson);
			}
			
			JSONObject obj = new JSONObject();
			obj.put("hooks", hooksJson);
			obj.put("defaults", defaultsJson);
			
			writer.write(obj.toJSONString());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void load() {
		
		try (FileReader reader = new FileReader(getSaveFile())) {
			JSONParser parser = new JSONParser();
			
			JSONObject obj = (JSONObject) parser.parse(reader);
			
			JSONArray hooksJson = (JSONArray) obj.get("hooks");
			

			
			for (int i = 0; i < hooksJson.size(); i++) {
				JSONObject hookJson = (JSONObject) hooksJson.get(i);
				
				try {
					Class clazz = Class.forName((String) hookJson.get("class"));
					Hook h = (Hook) clazz.newInstance();
					Job job = jobManager.getJob((String) hookJson.get("job"));
					if (job != null) {
						h.setJob(job);
						h.getProperties().putAll((Map<String, String>) hookJson.get("properties"));
						hooks.add(h);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			
			JSONArray defaultsJson = (JSONArray)obj.get("defaults");
			
			for(int i = 0; i < defaultsJson.size(); i++){
				JSONObject defaultJson = (JSONObject) defaultsJson.get(i);
				DefaultHook defaultHook = new DefaultHook();
				defaultHook.setHookClass((String) defaultJson.get("class"));
				defaultHook.getProperties().putAll((Map<String, String>) defaultJson.get("properties"));
				defaultHook.setRegex((String) defaultJson.get("regex"));
				defaults.add(defaultHook);
			}
			
			
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	private File getSaveFile() {
		File folder = new File(System.getProperty("user.dir") + "\\conf\\");
		folder.mkdirs();
		File file = new File(folder, "hooks.json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File: " + file);
		return file;
	}
	
	/**
	 * @param h
	 */
	public void addHook(Hook h) {
		hooks.add(h);
	}
	

	public void applyDefaults(Job job) {
		System.out.println("ApplyDefaults: " + job.getName());
		for (DefaultHook defaultHook : defaults) {
			defaultHook.applyIfMatch(job);
		}
	}
	
}
