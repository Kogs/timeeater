/**
 *
 */
package de.kogs.timeeater.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 
 *
 */
public class Settings {

	private static Properties prop = new Properties();
	private static boolean isLoaded = false;
	
	public static void load() {
		try (InputStream input = new FileInputStream(getSettingsFile())) {
			
			// load a properties file
			prop.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {


		try (OutputStream output = new FileOutputStream(getSettingsFile())) {
			prop.store(output, "Properties File for the TimeEater");
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	private static void checkInit(){
		if (!isLoaded) {
			load();
		}
	}
	
	public static String getProperty(String key) {
		checkInit();
		return prop.getProperty(key);
	}
	
	public static String getProperty(String key, String defaultValue) {
		checkInit();
		return prop.getProperty(key, defaultValue);
	}
	
	public static void setProperty(String key, String value) {
		checkInit();
		prop.setProperty(key, value);
		save();
	}
	
	private static File getSettingsFile() {
		File folder = new File(System.getProperty("user.dir") + "\\conf\\");
		folder.mkdirs();
		File file = new File(folder, "settings.properties");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static boolean booleanValue(String key, boolean defaultV) {
		return Boolean.valueOf(getProperty(key, String.valueOf(defaultV)));
	}
}

