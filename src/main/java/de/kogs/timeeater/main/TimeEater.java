/**
 *
 */
package de.kogs.timeeater.main;

import de.kogs.timeeater.controller.DialogController;
import de.kogs.timeeater.controller.StartController;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.tray.TimeEaterTray;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.SystemTray;
import java.util.concurrent.TimeUnit;

/**
 */
public class TimeEater extends Application {
	
	private TimeEaterTray trayIcon;
	private WorkTimeReminder reminder;
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Platform.setImplicitExit(false);
		
		StartController startController = new StartController(() -> {
			try {
				trayIcon = new TimeEaterTray();
				SystemTray.getSystemTray().add(trayIcon);
				
				reminder = new WorkTimeReminder(TimeUnit.MINUTES, 5, trayIcon);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			DialogController dia = new DialogController("Time Eater wurde gestartet",
					"Hallo " + System.getProperty("user.name"));
		});
	}
	
	@Override
	public void stop() throws Exception {
		
		JobProvider manager = JobProvider.getProvider();
		if (manager != null) {
			manager.stopWork();
			manager.save();
		}
		if (trayIcon != null) {
			SystemTray.getSystemTray().remove(trayIcon);
		}
		super.stop();
		System.exit(0);
	}
	
}
