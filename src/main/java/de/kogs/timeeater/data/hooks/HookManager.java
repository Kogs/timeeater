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
	
	public static HookManager instance() {
		if (instance == null) {
			instance = new HookManager();
		}
		return instance;
	}
	
	private List<Hook> hooks = new ArrayList<>();
	
	public static void main(String[] args) {
		instance();
	}
	
	public HookManager () {
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
			JSONObject obj = new JSONObject();
			obj.put("hooks", hooksJson);
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
			
			JobManager manager = JobManager.instance();
			
			for (int i = 0; i < hooksJson.size(); i++) {
				JSONObject hookJson = (JSONObject) hooksJson.get(i);
				
				try {
					Class clazz = Class.forName((String) hookJson.get("class"));
					Hook h = (Hook) clazz.newInstance();
					Job job = manager.getJob((String) hookJson.get("job"));
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
	
}
