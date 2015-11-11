/**
 *
 */
package de.kogs.timeeater.data;

import org.codehaus.jackson.map.ObjectMapper;

import de.kogs.timeeater.controller.DialogController;
import de.kogs.timeeater.data.hooks.HookManager;
import javafx.application.Platform;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class JobManager {

	private static JobManager instance;

	public static JobManager instance() {
		if (instance == null) {
			instance = new JobManager();
		}
		return instance;
	}

	public static HookManager hookInstance() {
		return HookManager.instance(instance);
	}
	
	private List<ManagerListener> listeners = new ArrayList<ManagerListener>();

	private Job activeJob;


	
	private Map<String, Job> kownJobs = new HashMap<>();

	/**
	 * 
	 */
	public JobManager() {
		load();
	}


	public Job getActiveJob() {
		return activeJob;
	}

	public void startWorkOnJob(String jobName) {
		if (kownJobs.containsKey(jobName)) {
			startWorkOnJob(kownJobs.get(jobName));
		} else {
			startWorkOnJob(createJob(jobName));
		}
	}
	
	private Job createJob(String name) {
		Job newJob = new Job();
		newJob.setName(name);
		kownJobs.put(name, newJob);
		return newJob;
	}

	private void startWorkOnJob(Job job) {
		if (activeJob == null || activeJob.getActiveWork() == null) {
			activeJob = job;
			LoggedWork work = new LoggedWork();
			work.setLogDate(new Date());
			work.setLogStart(System.currentTimeMillis());
			activeJob.setActiveWork(work);
			activeJob.getWorks().add(work);
			activeJobEvent();
		}
	}

	public void stopWork() {
		if (activeJob != null && activeJob.getActiveWork() != null) {
			LoggedWork activeWork = activeJob.getActiveWork();
			activeWork.setLogEnd(System.currentTimeMillis());
			System.out.println("Worked on Job: " + activeJob.getName() + " "
					+ (activeWork.getLogEnd() - activeWork.getLogStart()));
			activeJob.setActiveWork(null);
			activeJob = null;
			activeJobEvent();
			save();
		}
	}


	public Collection<Job> getKownJobs() {
		return kownJobs.values();
	}

	public Collection<Job> getJobsForDay(Date day) {
		List<Job> jobsForDay = new ArrayList<>();
		for (Job job : getKownJobs()) {
			if (!job.getWorkForDay(day).isEmpty()) {
				jobsForDay.add(job);
			}
		}
		return jobsForDay;
	}
	
	public long getTimeForDay(Date day) {
		long time = 0;
		for (Job job : getKownJobs()) {
			time += job.getWorkTime(day);
		}
		return time;
	}
	
	public List<Job> getJobsForRange(Date start, Date end) {
		List<Job> jobsForDay = new ArrayList<>();
		for (Job job : getKownJobs()) {
			if (!job.getWorkInRange(start, end).isEmpty()) {
				jobsForDay.add(job);
			}
		}
		return jobsForDay;
	}
	
	public Job getJob(String name) {
		if (kownJobs.containsKey(name)) {
			return kownJobs.get(name);
		}
		return null;
	}
	
	private void activeJobEvent() {
		for (ManagerListener listener : listeners) {
			listener.activeJobChanged(activeJob);
		}
	}

	public void addListener(ManagerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ManagerListener listener) {
		listeners.remove(listener);
	}
	
	public void save() {
		backup();
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(getSaveFileJson(), new ArrayList<>(getKownJobs()));
			Platform.runLater(() -> new DialogController("Speichern", "Daten wurden gespeichert"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		hookInstance().save();
	}

	private void backup() {
		
		File backupFile = getBackupFile();
		if (backupFile.exists()) {
//			Platform.runLater(() -> new DialogController("Fehler", "Backup Datei existiert bereits"));
			return;
		}
		
		try (GZIPOutputStream output = new GZIPOutputStream(new FileOutputStream(backupFile));
				FileInputStream input = new FileInputStream(getSaveFileJson())) {
				
			byte[] buffer = new byte[1024]; // Adjust if you want
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void load() {
		
		if (getSaveFile().exists()) {
			// read old xml save file
			// and save to new json File
			try (XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(getSaveFile())))) {

				List<Job> jobs = (List<Job>) d.readObject();
				kownJobs = jobs.stream().collect(Collectors.toMap(Job::getName, Function.identity()));
				save();
				kownJobs.clear();
				getSaveFile().delete();
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Job[] jobsArray = mapper.readValue(getSaveFileJson(), Job[].class);
			List<Job> jobs = Arrays.asList(jobsArray);
			
			kownJobs = jobs.stream().collect(Collectors.toMap(Job::getName, Function.identity()));
			
			// HOOKMANAGER DISABLED FIXME
//			HookManager hookManager = hookInstance();
//			for (Job job : jobs) {
//				hookManager.applyDefaults(job);
//			}
//			hookInstance().save();
			
		} catch (IOException e) {
			Platform.runLater(() -> new DialogController("Fehler", "Daten konnten nicht geladen werden"));
			e.printStackTrace();
		}
		

	}

	private File getSaveFile() {
		File folder = new File(System.getProperty("user.dir") + "\\conf\\");
		folder.mkdirs();
		File file = new File(folder, "save.xml");
		System.out.println("File: " + file);
		return file;
	}

	private File getSaveFileJson() {
		File folder = new File(System.getProperty("user.dir") + "\\conf\\");
		folder.mkdirs();
		File file = new File(folder, "save.json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File: " + file);
		return file;
	}
	
	private File getBackupFile() {
		File folder = new File(System.getProperty("user.dir") + "\\backup\\");
		folder.mkdirs();
		DateFormat format = new SimpleDateFormat("yyyy.MM.dd_HH");
		
		String fileName = "save_" + format.format(new Date()) + ".backup";
		File file = new File(folder, fileName);
		System.out.println("File: " + file);
		return file;
		
	}
	
	public void removeJob(Job j) {
		kownJobs.remove(j.getName());
		save();

	}

}
