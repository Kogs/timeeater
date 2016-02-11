/**
 *
 */
package de.kogs.timeeater.controller;


import de.kogs.javafx.decoratedScene.DecoratedScene;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.util.Utils;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class JobOverviewController extends Stage implements Initializable {
	
	private static final PseudoClass VIEWING_PSEUDO = PseudoClass.getPseudoClass("viewing");
	
	@FXML
	private TextField jobName;
	
	@FXML
	private TextArea descriptionArea;
	
	@FXML
	private BarChart<String, Number> lastWorkChart;
	private CategoryAxis xAxis;
	
	private NumberAxis yAxis;
	
	@FXML
	private RadioButton yearRadio;
	
	@FXML
	private RadioButton monthRadio;
	
	@FXML
	private RadioButton weekRadio;
	
	@FXML
	private ToggleGroup lastWorkToggleGroup;
	
	@FXML
	private Button saveButton;
	
	@FXML
	private Button editButton;
	
	@FXML
	private Label lastActive;
	
	@FXML
	private Label timeFull;
	
	@FXML
	private Label timeToday;
	
	@FXML
	private Label averangeTime;
	
	@FXML
	private Label countWork;
	
	private JobVo job;
	
	public JobOverviewController (JobVo job) {
		this.job = job;
		setTitle("Job Overview Window");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(JobOverviewController.class.getResource("/jobOverview.fxml"));
		initStyle(StageStyle.UNDECORATED);
		try {
			Scene scene = new DecoratedScene((Region) loader.load());
			scene.getStylesheets().add("style.css");
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		show();
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		jobName.setText(job.getName());
		descriptionArea.setText(job.getDescription());
		jobName.pseudoClassStateChanged(VIEWING_PSEUDO, true);
		descriptionArea.pseudoClassStateChanged(VIEWING_PSEUDO, true);
		
		xAxis = (CategoryAxis) lastWorkChart.getXAxis();
		yAxis = (NumberAxis) lastWorkChart.getYAxis();
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {
			
			@Override
			public String toString(Number object) {
				long millis = object.longValue();
				return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
						TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
			}
			
			@Override
			public Number fromString(String string) {
				return null;
			}
		});
		
		lastWorkToggleGroup.selectedToggleProperty().addListener((obs) -> {
			loadLastWorkChart();
		});
		loadLastWorkChart();
		
		lastActive.setText("Zuletzt Aktiv: " + job.getLastWork().getLogDate());
		long fullWorkTime = job.getFullWorkTime();
		timeFull.setText("Laufzeit gesamt: " + Utils.millisToString(fullWorkTime));
		timeToday.setText("Laufzeit heute: " + Utils.millisToString(job.getWorkTime(new Date())));
		
		long averagenTime = fullWorkTime / job.getWorks().size();
		
		averangeTime.setText("Durschnittliche Zeit: " + Utils.millisToString(averagenTime));
		countWork.setText("Anzahl Aktiv: " + job.getWorks().size());
	}
	
	@FXML
	private void edit() {
		editButton.setVisible(false);
		editButton.setManaged(false);
		saveButton.setVisible(true);
		saveButton.setManaged(true);
		jobName.setEditable(true);
		descriptionArea.setEditable(true);
		jobName.pseudoClassStateChanged(VIEWING_PSEUDO, false);
		descriptionArea.pseudoClassStateChanged(VIEWING_PSEUDO, false);
	}
	
	@FXML
	private void save() {
		editButton.setVisible(true);
		editButton.setManaged(true);
		saveButton.setVisible(false);
		saveButton.setManaged(false);
		jobName.setEditable(false);
		descriptionArea.setEditable(false);
		jobName.pseudoClassStateChanged(VIEWING_PSEUDO, true);
		descriptionArea.pseudoClassStateChanged(VIEWING_PSEUDO, true);
		
		job.setName(jobName.getText());
		job.setDescription(descriptionArea.getText());
		JobProvider.getProvider().save();
	}
	

	
	@FXML
	private void addHook() {
//		Popup popup = new Popup();
//		
//		ComboBox<Class<? extends Hook>> hookSelector = new ComboBox<>();
//		hookSelector.setConverter(new StringConverter<Class<? extends Hook>>() {
//			
//			@Override
//			public String toString(Class<? extends Hook> object) {
//				return object.getSimpleName();
//			}
//			
//			@Override
//			public Class<? extends Hook> fromString(String string) {
//				try {
//					return (Class<? extends Hook>) Class.forName("de.kogs.timeeater.data.hooks." + string);
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//		});
//		
//		Reflections reflections = new Reflections("de.kogs.timeeater.data.hooks");
//		
//
//		Set<Class<? extends Hook>> classes = reflections.getSubTypesOf(Hook.class);
//		// TODO filer already setted Hooks
//		for (Class<? extends Hook> hookClass : classes) {
//			HookConfig config = hookClass.getAnnotation(HookConfig.class);
//			boolean allowMultiple = config != null ? !config.singleton() : true;
//			if (!allowMultiple) {
//				if (JobManager.hookInstance().getHookForJob(job, hookClass) == null) {
//					hookSelector.getItems().add(hookClass);
//				}
//				
//			} else {
//				hookSelector.getItems().add(hookClass);
//			}
//		}
//
//		
//		StackPane hookPane = new StackPane();
//		Button ok = new Button("Ok");
//		ok.setOnAction((e)->{
//			currentEditingHook.getGuiContent().submit();
//			hooksListView.getItems().add(currentEditingHook);
//			JobManager.hookInstance().addHook(currentEditingHook);
//			popup.hide();
//			ok.disableProperty().unbind();
//		});
//		VBox root = new VBox(hookSelector, hookPane,ok);
//		
//		root.setFillWidth(true);
//		
//		root.setStyle("-fx-background-color: gray");
//		
//		hookSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
//			try {
//				currentEditingHook = newV.newInstance();
//				currentEditingHook.setJob(job);
//				HookConfigGui guiContent = currentEditingHook.getGuiContent();
//				hookPane.getChildren().setAll(guiContent.getGui());
//				ok.disableProperty().bind(guiContent.submitSupportedBinding().not());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		
//		popup.getContent().add(root);
//		
//		Bounds bounds = hooksListView.localToScreen(hooksListView.getBoundsInLocal());
//		
//		popup.setAutoHide(true);
//		
//		popup.show(this, bounds.getMinX(), bounds.getMaxY());
//		hookSelector.getSelectionModel().select(0);

	}
	
	@FXML
	private void editHook() {
	
	}
	
	@FXML
	private void deleteHook() {
	
	}
	
	private void loadLastWorkChart() {
		lastWorkChart.getData().clear();
		
		if (yearRadio.isSelected()) {
			lastYear();
		} else if (monthRadio.isSelected()) {
			lastMonth();
		} else if (weekRadio.isSelected()) {
			lastWeek();
		}
	}
	
	private void lastYear() {
		Series<String, Number> months = new Series<String, Number>();
		
		Calendar c = GregorianCalendar.getInstance(Locale.GERMAN);
		c.setTime(new Date());
		DateFormat format = new SimpleDateFormat("MMMM");
		
		for (int i = 0; i < 12; i++) {
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = c.getTime();
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = c.getTime();
			months.getData().add(0,
					new Data<String, Number>(format.format(startDate), job.getWorkTimeInRange(startDate, endDate)));
			c.add(Calendar.MONTH, -1);
		}
		
		lastWorkChart.getData().setAll(months);
	}
	
	private void lastMonth() {
		Series<String, Number> weeks = new Series<String, Number>();
		
		Calendar c = GregorianCalendar.getInstance(Locale.GERMAN);
		c.setTime(new Date());
		DateFormat format = new SimpleDateFormat("dd.MM");
		
		for (int i = 0; i < 4; i++) {
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			Date weekStartDate = c.getTime();
			c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			Date weekEndDate = c.getTime();
			
			weeks.getData().add(0, new Data<String, Number>(format.format(weekStartDate) + "-" + format.format(weekEndDate),
					job.getWorkTimeInRange(weekStartDate, weekEndDate)));
			c.add(Calendar.DAY_OF_WEEK, -7);
		}
		
		lastWorkChart.getData().setAll(weeks);
		
	}
	
	private void lastWeek() {
		Series<String, Number> days = new Series<String, Number>();
		
		Calendar c = GregorianCalendar.getInstance(Locale.GERMAN);
		c.setTime(new Date());
		DateFormat dayName = new SimpleDateFormat("EEEE");
		
		for (int i = 0; i < 5; i++) {
			while (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_WEEK, -1);
			}
			days.getData().add(0, new Data<>(dayName.format(c.getTime()), job.getWorkTime(c.getTime())));
			c.add(Calendar.DAY_OF_WEEK, -1);
		}
		
		lastWorkChart.getData().setAll(days);
		
	}
	
}
