package de.kogs.timeeater.controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import de.kogs.timeeater.chart.ReloadChartListener;
import de.kogs.timeeater.chart.TimeChart;
import de.kogs.timeeater.chart.TimeChart.ExtraData;
import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.LoggedWork;
import de.kogs.timeeater.util.Utils;

public class DayOverviewController extends Stage implements Initializable, ReloadChartListener {

	private StringConverter<Number> xAxisStringConverter = new StringConverter<Number>() {

		@Override
		public String toString(Number object) {
			return Utils.timeToString(object.longValue());
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

	@FXML
	private Label dateLabel;

	@FXML
	private ComboBox<Job> jobSelector;

	private CategoryAxis yAxis;

	private NumberAxis xAxis;

	private TimeChart chart;

	public DayOverviewController(Date day) {
		this.day = day;
		setTitle("Day Overview Window");

		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class
				.getResource("/dayOverview.fxml"));

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

		dateLabel.setText(new SimpleDateFormat("EEEE dd.MM.yyyy").format(day));

		yAxis = new CategoryAxis();
		xAxis = new NumberAxis(getFirstTimeOfDay(),
				getLastTimeOfDay(), TimeUnit.MINUTES.toMillis(30));

		xAxis.setTickLabelFormatter(xAxisStringConverter);

		chart = new TimeChart(xAxis, yAxis,this);
		chart.setData(FXCollections.observableArrayList());
		AnchorPane.setTopAnchor(chart, 0d);
		AnchorPane.setLeftAnchor(chart, 0d);
		AnchorPane.setBottomAnchor(chart, 0d);
		AnchorPane.setRightAnchor(chart, 0d);
		chartContainer.getChildren().add(chart);

		jobSelector.getItems().addAll(JobManager.instance().getKownJobs());
		loadData();
	}

	@Override
	public void reload() {
		loadData();
		JobManager.instance().save();
	}
	
	private void loadData() {
		JobManager manager = JobManager.instance();

		yAxis.getCategories().clear();
		chart.getData().clear();
		
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
				Data<Number, String> aData = new Data<Number, String>(
						work.getLogStart(), job.getName(), new ExtraData(end
								- work.getLogStart(), job, work));

				jobSeries.getData().add(aData);
			}

		}
	}

	@FXML
	private void addWork() {
		Job selectedJob = jobSelector.getSelectionModel().getSelectedItem();
		if (selectedJob != null) {

		}
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