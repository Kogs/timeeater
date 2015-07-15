/**
 *
 */
package de.kogs.timeeater.controller;

import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.hooks.Hook;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
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
	private ListView<Hook> hooksListView;
	
	private Job job;
	
	public JobOverviewController (Job job) {
		this.job = job;
		setTitle("Job Overview Window");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(JobOverviewController.class.getResource("/jobOverview.fxml"));
		
		try {
			Scene scene = new Scene((Parent) loader.load());
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
		JobManager.instance().save();
	}
	
	@FXML
	private void addHook() {
	
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
