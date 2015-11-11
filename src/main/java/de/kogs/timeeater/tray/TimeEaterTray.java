/**
 *
 */
package de.kogs.timeeater.tray;

import de.kogs.timeeater.controller.LoggerController;
import de.kogs.timeeater.controller.OverviewController;
import de.kogs.timeeater.controller.QuickLinkController;
import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.ManagerListener;
import javafx.application.Platform;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class TimeEaterTray extends TrayIcon implements ManagerListener {

	/**
	 * @param image
	 * @throws IOException
	 */
	public TimeEaterTray() throws IOException {
		super(ImageIO.read(TimeEaterTray.class
				.getResourceAsStream("/images/tray.png")));

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

		JobManager.instance().addListener(this);
		setToolTip("Keine Vorgang aktiv");
	}

	private void createPopup() {
		final PopupMenu popup = new PopupMenu();
		MenuItem aboutItem = new MenuItem("About");

		Menu hooks = new Menu("Hooks");
		
		MenuItem quickLinks = new MenuItem("QuickLinks");
		hooks.add(quickLinks);
		quickLinks.addActionListener(e -> Platform.runLater(() -> new QuickLinkController()));
		
		MenuItem overview = new MenuItem("Overview");
		overview.addActionListener(e -> Platform.runLater(()->new OverviewController()));

		MenuItem save = new MenuItem("Speichern");
		save.addActionListener(e -> JobManager.instance().save());

		MenuItem exitItem = new MenuItem("Beenden");
		exitItem.addActionListener(e -> Platform.runLater(() -> Platform.exit()));

		// Add components to pop-up menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(hooks);
		popup.add(overview);
		popup.addSeparator();
		popup.add(save);

		popup.addSeparator();
		popup.add(exitItem);

		setPopupMenu(popup);
	}

	// private void showPopup() {
	// Popup popup = new Popup();
	// popup.get
	// }

	private void click() {
		Platform.runLater(() -> {
			LoggerController controller = new LoggerController();
		});

	}

	@Override
	public void activeJobChanged(Job activeJob) {
		if (activeJob != null) {
			setToolTip("Vorgang aktiv: " + activeJob.getName());
		} else {
			setToolTip("Keine Vorgang aktiv");
		}
	}

}
