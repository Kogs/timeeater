package de.kogs.timeeater.controller;

import de.kogs.timeeater.chart.TimeChart;
import de.kogs.timeeater.chart.TimeChart.ExtraData;
import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.LoggedWork;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class DayOverviewController extends Stage implements Initializable {
	
	private StringConverter<Number> xAxisStringConverter = new StringConverter<Number>() {
		
		@Override
		public String toString(Number object) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis(object.longValue());
			
			return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		}
		
		@Override
		public Number fromString(String string) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	private Date day;
	
	@FXML
	private AnchorPane chartContainer;
	
	public DayOverviewController (Date day) {
		this.day = day;
		setTitle("Day Overview Window");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class.getResource("/dayOverview.fxml"));
		
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
		
		CategoryAxis yAxis = new CategoryAxis();
		NumberAxis xAxis = new NumberAxis(getFirstTimeOfDay(), getLastTimeOfDay(), TimeUnit.MINUTES.toMillis(30));
		
		xAxis.setTickLabelFormatter(xAxisStringConverter);
		
		TimeChart<Number, String> chart = new TimeChart<>(xAxis, yAxis);
		chart.setData(FXCollections.observableArrayList());
		
		JobManager manager = JobManager.instance();
		
		for (Job job : manager.getKownJobs()) {
			yAxis.getCategories().add(job.getName());
		}
		
		for (Job job : manager.getKownJobs()) {
			
			Series<Number, String> jobSeries = new Series<>();
			chart.getData().add(jobSeries);
			for (LoggedWork work : job.getWorkForDay(day)) {
				long end = System.currentTimeMillis();
				if (work.getLogEnd() != null) {
					end = work.getLogEnd();
				}
				Data<Number, String> aData = new Data<Number, String>(work.getLogStart(), job.getName(),
						new ExtraData(end - work.getLogStart(), job, work));
						
				jobSeries.getData().add(aData);
			}
			
		}
		
		AnchorPane.setTopAnchor(chart, 0d);
		AnchorPane.setLeftAnchor(chart, 0d);
		AnchorPane.setBottomAnchor(chart, 0d);
		AnchorPane.setRightAnchor(chart, 0d);
		chartContainer.getChildren().add(chart);
	}
	
	private long getFirstTimeOfDay() {
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(day);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 7);
		
		return cal.getTime().getTime();
	}
	
	private long getLastTimeOfDay() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(day);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 19);
		
		return cal.getTime().getTime();
	}
	
}
