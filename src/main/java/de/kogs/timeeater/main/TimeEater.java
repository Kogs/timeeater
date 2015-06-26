/**
 *
 */
package de.kogs.timeeater.main;

import java.awt.SystemTray;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import de.kogs.timeeater.tray.TimeEaterTray;

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
		SystemTray tray = SystemTray.getSystemTray();
		tray.add(trayIcon);
	}
	
}
