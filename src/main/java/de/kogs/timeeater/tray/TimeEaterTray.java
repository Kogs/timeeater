/**
 *
 */
package de.kogs.timeeater.tray;

import de.kogs.timeeater.controller.DialogController;
import de.kogs.timeeater.controller.JobsController;
import de.kogs.timeeater.controller.LoggerController;
import de.kogs.timeeater.controller.OverviewController;
import de.kogs.timeeater.controller.QuickLinkController;
import de.kogs.timeeater.controller.WakeUpController;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.data.ManagerListener;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

/**
 * 
 */
public class TimeEaterTray extends TrayIcon implements ManagerListener {
	
	private Image image1;
	private Image image2;
	private Image image3;
	private Image image4;
	private PauseTransition trayAnimation;
	private String timeLeftMsg = "";
	private JobVo activeJob;
	
	/**
	 * @param image
	 * @throws IOException
	 */
	public TimeEaterTray () throws IOException {
		super(ImageIO.read(TimeEaterTray.class.getResourceAsStream("/images/tray.png")));
		
		image1 = ImageIO.read(TimeEaterTray.class.getResourceAsStream("/images/tray.png"));
		image2 = ImageIO.read(TimeEaterTray.class.getResourceAsStream("/images/tray_2.png"));
		image3 = ImageIO.read(TimeEaterTray.class.getResourceAsStream("/images/tray_3.png"));
		image4 = ImageIO.read(TimeEaterTray.class.getResourceAsStream("/images/tray_4.png"));
		setImage(image1);
		
		trayAnimation = new PauseTransition(Duration.seconds(0.6));
		trayAnimation.setOnFinished((e) -> {
			Image image = getImage();
			if (image == image1) {
				setImage(image2);
			} else if (image == image2) {
				setImage(image3);
			} else if (image == image3) {
				setImage(image4);
			} else if (image == image4) {
				setImage(image1);
			}
			trayAnimation.play();
		});
//		trayAnimation.play();
		
		setImageAutoSize(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						click();
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						// showPopup();
					}
					
				}
			}
		});
		createPopup();
		
		JobProvider.getProvider().addListener(this);
		setToolTip("No Job Active");
	}
	
	private void createPopup() {
		final PopupMenu popup = new PopupMenu();
		MenuItem aboutItem = new MenuItem("About");
		
		aboutItem.addActionListener(e -> Platform.runLater(this::about));
		
		MenuItem wakeUp = new MenuItem("WakeUp");
		wakeUp.addActionListener(e -> Platform.runLater(() -> new WakeUpController()));
		Menu hooks = new Menu("Hooks");
		
		MenuItem quickLinks = new MenuItem("QuickLinks");
		hooks.add(quickLinks);
		quickLinks.addActionListener(e -> Platform.runLater(() -> new QuickLinkController()));
		
		MenuItem jobs = new MenuItem("Jobs");
		jobs.addActionListener(e -> Platform.runLater(() -> new JobsController()));
		
		MenuItem overview = new MenuItem("Overview");
		overview.addActionListener(e -> Platform.runLater(() -> new OverviewController()));
		
		MenuItem save = new MenuItem("Speichern");
		save.addActionListener(e -> JobProvider.getProvider().save());
		
		MenuItem backup = new MenuItem("Backup");
		backup.addActionListener(e -> JobProvider.getProvider().backup());
		
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(e -> Platform.runLater(() -> Platform.exit()));
		
		// Add components to pop-up menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(wakeUp);
		popup.addSeparator();
		popup.add(hooks);
		popup.add(jobs);
		popup.add(overview);
		popup.addSeparator();
		popup.add(save);
		popup.add(backup);
		popup.addSeparator();
		popup.add(exitItem);
		
		setPopupMenu(popup);
	}
	
	// private void showPopup() {
	// Popup popup = new Popup();
	// popup.get
	// }
	
	private void about() {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(new URI("http://www.thehardcoders.de/#/projects/project/2"));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			DialogController dialog = new DialogController("TimeEater About",
					"TimeEater by Marcel Vogel all Rights reserved");
			dialog.show();
		}
	}
	
	private void click() {
		Platform.runLater(() -> {
			LoggerController controller = new LoggerController();
		});
		
	}
	
	@Override
	public void activeJobChanged(JobVo activeJob) {
		this.activeJob = activeJob;
		if (activeJob != null) {
			trayAnimation.play();
		} else {
			trayAnimation.pause();
		}
		updateToolTip();
	}
	
	public void updateTimeLeft(String msg) {
		timeLeftMsg = "\n" + msg;
		updateToolTip();
	}
	
	public void updateToolTip() {
		if (activeJob != null) {
			setToolTip("Active Job: " + activeJob.getName() + timeLeftMsg);
		} else {
			setToolTip("No Job active" + timeLeftMsg);
		}
	}
	
}
