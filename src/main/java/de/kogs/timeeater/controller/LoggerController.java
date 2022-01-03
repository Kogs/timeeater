/**
 *
 */
package de.kogs.timeeater.controller;

import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.data.LoggedWork;
import de.kogs.timeeater.data.comparator.LastWorkComparator;
import de.kogs.timeeater.main.TimeEater;
import de.kogs.timeeater.util.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class LoggerController extends Stage implements Initializable {
	
	/**
	 * 
	 */
	public LoggerController () {
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
		
		Rectangle2D visialBounds = Screen.getPrimary().getVisualBounds();
		Rectangle2D bounds = Screen.getPrimary().getBounds();
		
		DoubleProperty stageXProperty = new SimpleDoubleProperty(0);
		stageXProperty.addListener((obs, old, newV) -> {
			setX(newV.doubleValue());
		});
		DoubleProperty stageYProperty = new SimpleDoubleProperty(0);
		stageYProperty.addListener((obs, old, newV) -> {
			setY(newV.doubleValue());
		});
		
		show();
		
		setX(visialBounds.getMaxX() - getWidth());
		setY(bounds.getMaxY());
		Timeline showLine = new Timeline();
		showLine.getKeyFrames()
				.add(new KeyFrame(Duration.ZERO, new KeyValue(stageXProperty, bounds.getMaxX() - getWidth())));
		showLine.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(stageYProperty, bounds.getMaxY())));
		
		showLine.getKeyFrames()
				.add(new KeyFrame(Duration.seconds(0.3), new KeyValue(stageXProperty, visialBounds.getMaxX() - getWidth())));
		showLine.getKeyFrames()
				.add(new KeyFrame(Duration.seconds(0.3), new KeyValue(stageYProperty, visialBounds.getMaxY() - getHeight())));
				
		showLine.play();
		
		focusedProperty().addListener((obs) -> {
			if (!isFocused()) {
				closeLogger();
			}
		});
		getIcons().add(TimeEater.STAGE_ICON);
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
	private ComboBox<String> workSelector;

	@FXML
	private TextField workSelectorField;
	
	private JobProvider provider;
	
	private PauseTransition refreshLabels;
	
	private List<String> workList = new ArrayList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		provider = JobProvider.getProvider();
		
		Date lastWeek = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastWeek);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		
		List<JobVo> allJobs = new ArrayList<>(provider.getKownJobs());
		Collections.sort(allJobs, new LastWorkComparator());
	
		workList.addAll(allJobs.stream().limit(10).map(Objects::toString).collect(Collectors.toList()));
		workSelector.getItems().addAll(workList);
		
		workSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV)-> {
			if (newV != null) {
				workSelectorField.setText(newV);
			}
		});
		workSelectorField.textProperty().addListener((obs, oldV ,newV)-> {
			if (!newV.trim().isEmpty()) {
				Set<String> filteredJobs = new HashSet<>();
				for (JobVo vo : allJobs) {
					if (vo.getName().toLowerCase().startsWith(newV.toLowerCase())) {
						filteredJobs.add(vo.getName());
						if(filteredJobs.size() > 5){
							break;
						}
					}
				}
				workSelector.getItems().setAll(filteredJobs);
				workSelector.show();
			} else {
				workSelector.hide();
				workSelector.getItems().setAll(workList);
			}	
		});
		
		refreshLabels = new PauseTransition(Duration.seconds(1));
		refreshLabels.setOnFinished((event) -> {
			updateLabels();
			refreshLabels.play();
		});
		
		boolean workActive = provider.getActiveJob() != null && provider.getActiveJob().getActiveWork() != null;
		
		if (workActive) {
			workSelectorField.setText(provider.getActiveJob().getName());
			updateLabels();
			refreshLabels.play();
		} else {
			runningSince.setText("Kein Vorgang aktiv");
			runningTime.setText("");
			runningTimeFull.setText("");
			runningTimeToday.setText("");
			Platform.runLater(() -> workSelectorField.requestFocus());
		}
		startButton.setDisable(workActive);
		stopButton.setDisable(!workActive);
		workSelector.setDisable(workActive);
		workSelectorField.setDisable(workActive);
	}
	
	private void updateLabels() {
		JobVo activeJob = provider.getActiveJob();
		if (activeJob != null && activeJob.getActiveWork() != null) {
			
			LoggedWork activeWork = activeJob.getActiveWork();
			runningSince.setText("Gestartet um: " + activeWork.getLogDate());
			runningTime.setText("Laufzeit: " + Utils.millisToString((System.currentTimeMillis() - activeWork.getLogStart())));
			runningTimeFull.setText("Laufzeit gesamt: " + Utils.millisToString(activeJob.getFullWorkTime()));
		}
	}
	
	@FXML
	private void startWork() {
		final String job = workSelectorField.getText();
		if (job.trim().isEmpty()){
			return;
		}
		provider.startWorkOnJob(job);
		closeLogger();
	}
	
	@FXML
	private void stopWork() {
		provider.stopWork();
		closeLogger();
	}
	
	@FXML
	private void closeLogger() {
		close();
		refreshLabels.stop();
	}
	
}
