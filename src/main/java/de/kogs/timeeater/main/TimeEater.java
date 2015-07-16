/**
 *
 */
package de.kogs.timeeater.main;

import de.kogs.timeeater.controller.DialogController;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.tray.TimeEaterTray;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.SystemTray;

/**
 */
public class TimeEater extends Application {
	
	private TimeEaterTray trayIcon;
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Platform.setImplicitExit(false);
		trayIcon = new TimeEaterTray();
		SystemTray.getSystemTray().add(trayIcon);
		DialogController dia = new DialogController("Time Eater wurde gestartet", "Hallo " + System.getProperty("user.name"));
	}
	
	
	
	@Override
	public void stop() throws Exception {
		
		JobManager manager = JobManager.instance();
		manager.stopWork();
		manager.save();
		
		SystemTray.getSystemTray().remove(trayIcon);
		super.stop();
	}
	
}
