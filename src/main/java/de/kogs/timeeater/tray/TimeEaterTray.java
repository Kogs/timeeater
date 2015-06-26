/**
 *
 */
package de.kogs.timeeater.tray;

import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javafx.application.Platform;

import javax.imageio.ImageIO;

import de.kogs.timeeater.controller.LoggerController;
import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.ManagerListener;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class TimeEaterTray extends TrayIcon implements ManagerListener{
	
	/**
	 * @param image
	 * @throws IOException
	 */
	public TimeEaterTray () throws IOException {
		super(ImageIO.read(TimeEaterTray.class.getResourceAsStream("/images/tray.png")));
		
		setImageAutoSize(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					click();
				}
			}
		});
		
		
		
		
		JobManager.instance().addListener(this);
		setToolTip("Keine Vorgang aktiv");
	}
	
	private void click() {
		
		Platform.runLater(() -> {
			LoggerController controller = new LoggerController();
		});
		
	}

	@Override
	public void activeJobChanged(Job activeJob) {
		if(activeJob != null){
			setToolTip("Vorgang aktiv: " + activeJob.getName());
		}else{
			setToolTip("Keine Vorgang aktiv");
		}
	}
	
}
