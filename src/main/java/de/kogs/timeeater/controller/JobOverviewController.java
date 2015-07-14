/**
 *
 */
package de.kogs.timeeater.controller;

import de.kogs.timeeater.data.Job;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class JobOverviewController extends Stage implements Initializable {
	

	@FXML
	private Label nameLabel;
	
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
		nameLabel.setText(job.getName());
		
		
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
	
	private void loadLastWorkChart() {
		lastWorkChart.getData().clear();
		
		if (yearRadio.isSelected()) {
			
		} else if (monthRadio.isSelected()) {
		
		} else if (weekRadio.isSelected()) {
			lastWeek();
		}
	}
	

	private void lastWeek() {
		
		Series<String, Number> days = new Series<String, Number>();
		
		Calendar c = GregorianCalendar.getInstance(Locale.GERMAN);
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Data<String, Number> monday = new Data<>("Montag", job.getWorkTime(c.getTime()));
		days.getData().add(monday);


		c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		Data<String, Number> tuesday = new Data<>("Dienstag", job.getWorkTime(c.getTime()));
		days.getData().add(tuesday);
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		Data<String, Number> wednesday = new Data<>("Mittwoch", job.getWorkTime(c.getTime()));
		days.getData().add(wednesday);
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		Data<String, Number> thursday = new Data<>("Donnerstag", job.getWorkTime(c.getTime()));
		days.getData().add(thursday);
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		Data<String, Number> friday = new Data<>("Freitag", job.getWorkTime(c.getTime()));
		days.getData().add(friday);
		
		lastWorkChart.getData().setAll(days);
		
	}
	

}
