/**
 *
 */
package de.kogs.timeeater.controller;

import de.kogs.javafx.decoratedScene.DecoratedScene;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.data.Settings;
import de.kogs.timeeater.main.TimeEater;
import de.kogs.timeeater.util.Utils;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class WakeUpController extends Stage implements Initializable {
	
	private static PauseTransition wait;
	private static Long targetTimeToShow = System.currentTimeMillis();
	
	public static void showWakeUpIn(Long millis) {
		targetTimeToShow = System.currentTimeMillis() + millis;
		wait = new PauseTransition(Duration.millis(millis));
		wait.setOnFinished((e) -> new WakeUpController());
		wait.play();
	}
	
	public WakeUpController () {
		setTitle("TimeEater WakeUp");
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class.getResource("/wakeUp.fxml"));
		initStyle(StageStyle.UNDECORATED);
		setAlwaysOnTop(true);
		centerOnScreen();
		try {
			Scene scene = new DecoratedScene((Region) loader.load());
			scene.getStylesheets().add("style.css");
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getIcons().add(TimeEater.STAGE_ICON);
		show();
		setOnHiding((e) -> {
			save();
			if (wakeUp.isSelected()) {
				showWakeUpIn(wakeUpInterval.getSelectionModel().getSelectedItem().longValue());
			}
		});
		if (wait != null) {
			wait.stop();
		}
	}
	
	@FXML
	private Label activeWorkLabel;
	
	@FXML
	private CheckBox wakeUp;
	
	@FXML
	private ComboBox<Number> wakeUpInterval;


	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		wakeUpInterval.setConverter(Utils.longToMillisConverter);
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(1)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(15)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(30)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(60)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(90)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(120)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(180)));
		wakeUpInterval.getSelectionModel()
				.select(Long.valueOf(Settings.getProperty("wakeup.interval", String.valueOf(TimeUnit.MINUTES.toMillis(30)))));
				
		wakeUpInterval.disableProperty().bind(wakeUp.selectedProperty().not());
		wakeUp.setSelected(Boolean.parseBoolean(Settings.getProperty("wakeup.active", "false")));
		JobVo activeJob = JobProvider.getProvider().getActiveJob();
		if(activeJob != null){
			activeWorkLabel.setText(activeJob.getName());
		}else{
			activeWorkLabel.setText("You are not Loggin anything");
		}
	}
	
	@FXML
	void ok() {
		close();
	}
	
	@FXML
	void showLog() {
		LoggerController controller = new LoggerController();
	}
	
	private void save() {
		Settings.setProperty("wakeup.active", Boolean.toString(wakeUp.isSelected()));
		Settings.setProperty("wakeup.interval", wakeUpInterval.getSelectionModel().getSelectedItem().toString());
	}
}
