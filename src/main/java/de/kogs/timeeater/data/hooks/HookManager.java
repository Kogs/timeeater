/**
 *
 */
package de.kogs.timeeater.data.hooks;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.kogs.timeeater.data.JobVo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

	private List<QuickLink> quickLinks = new ArrayList<>();
	
	
	public HookManager () {
		load();
	}
	

	
	public QuickLink getQuickLinkForJob(JobVo job) {
		for (QuickLink link : quickLinks) {
			if (Pattern.matches(link.getPattern(), job.getName())) {
				return link;
			}
			
		}
		return null;
	}
	
	
	public void save() {
		try (FileWriter writer = new FileWriter(getSaveFile())) {
			
			JSONArray quickLinksJson = new JSONArray();
			for (QuickLink link : quickLinks) {
				JSONObject linkJson = new JSONObject();
				linkJson.put("pattern", link.getPattern());
				linkJson.put("url", link.getUrl());
				quickLinksJson.add(linkJson);
			}

			JSONObject obj = new JSONObject();
			obj.put("quickLinks", quickLinksJson);
			
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
			
			JSONArray quickLinksJson = (JSONArray) obj.get("quickLinks");
		
			quickLinks.clear();
			for (int i = 0; i < quickLinksJson.size(); i++) {
				JSONObject linkJson = (JSONObject) quickLinksJson.get(i);
				QuickLink link = new QuickLink();
				link.setPattern((String)linkJson.get("pattern"));
				link.setUrl((String) linkJson.get("url"));
				quickLinks.add(link);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	private File getSaveFile() {
		File folder = new File(System.getProperty("user.dir") + "\\conf\\");
		folder.mkdirs();
		File file = new File(folder, "newHooks.json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File: " + file);
		return file;
	}

	public List<QuickLink> getQuickLinks() {
		return quickLinks;
	}
	
}
