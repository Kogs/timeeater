/**
 *
 */
package de.kogs.timeeater.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.LoggedWork;
import de.kogs.timeeater.util.Utils;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class LoggerController extends Stage implements Initializable {

	/**
	 * 
	 */
	public LoggerController() {
		setTitle("Logger Window");
		initStyle(StageStyle.UNDECORATED);
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(LoggerController.class.getResource("/log.fxml"));
		try {
			Scene scene = new Scene((Parent) loader.load());
			setScene(scene);
			scene.getStylesheets().add("style.css");
			scene.getRoot().getStyleClass().add("logger");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

		show();
		setX(bounds.getMaxX() - getWidth());
		setY(bounds.getMaxY() - getHeight());

		focusedProperty().addListener((obs) -> {
			if (!isFocused()) {
				closeLogger();
			}
		});
	}

	@FXML
	private Label runningTime;

	@FXML
	private Label runningSince;
	@FXML
	private Label runningTimeFull;
	@FXML
	private Label runningTimeToday;

	@FXML
	private Button startButton;

	@FXML
	private Button stopButton;

	@FXML
	private ComboBox<Job> workSelector;

	private JobManager manager;

	private PauseTransition refreshLabels;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		manager = JobManager.instance();
		workSelector.getItems().addAll(manager.getKownJobs());

		refreshLabels = new PauseTransition(Duration.seconds(1));
		refreshLabels.setOnFinished((event) -> {
			updateLabels();
			refreshLabels.play();
		});

		boolean workActive = manager.getActiveJob() != null
				&& manager.getActiveJob().getActiveWork() != null;

		if (workActive) {
			workSelector.getSelectionModel().select(manager.getActiveJob());
			updateLabels();
			refreshLabels.play();
		} else {
			runningSince.setText("Kein Vorgang aktiv");
			runningTime.setText("");
			runningTimeFull.setText("");
			runningTimeToday.setText("");
		}
		startButton.setDisable(workActive);
		stopButton.setDisable(!workActive);
		workSelector.setDisable(workActive);
	}

	private void updateLabels() {
		Job activeJob = manager.getActiveJob();
		if (activeJob != null && activeJob.getActiveWork() != null) {

			LoggedWork activeWork = activeJob.getActiveWork();
			runningSince.setText("Gestartet um: " + activeWork.getLogDate());
			runningTime
					.setText("Laufzeit: "
							+ Utils.millisToString((System.currentTimeMillis() - activeWork
									.getLogStart())));
			runningTimeFull.setText("Laufzeit gesamt: "
					+ Utils.millisToString(activeJob.getFullWorkTime()));
		}
	}

	@FXML
	private void startWork() {
		manager.startWorkOnJob(workSelector.getEditor().getText());
		closeLogger();
	}

	@FXML
	private void stopWork() {
		manager.stopWork();
		closeLogger();
	}

	@FXML
	private void closeLogger() {
		close();
		refreshLabels.stop();
	}

}
